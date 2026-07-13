const API = 'http://localhost:8080';

function getAuthHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + getToken()
    };
}


function handleScrollAnimations() {
    const elements = document.querySelectorAll('.fade-in');
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
            }
        });
    }, {
        threshold: 0.1
    });
    
    elements.forEach(el => observer.observe(el));
}

// Clear Errors
function clearErrors() {
    document.querySelectorAll('.form-group').forEach(group => {
        group.classList.remove('error');
        const errorMsg = group.querySelector('.error-message');
        if (errorMsg) errorMsg.textContent = '';
    });
}


function showError(fieldId, message) {
    const group = document.getElementById('group-' + fieldId);
    if (group) {
        group.classList.add('error');
        const errorMsg = group.querySelector('.error-message');
        if (errorMsg) errorMsg.textContent = message;
    }
}


function validateForm() {
    clearErrors();
    let isValid = true;
    
    const mesa = document.getElementById('select-mesa').value;
    const inicio = document.getElementById('data-inicio').value;
    const fim = document.getElementById('data-fim').value;
    const pessoas = document.getElementById('numero-pessoas').value;
    
    if (!mesa) {
        showError('mesa', 'Selecione uma mesa');
        isValid = false;
    }
    
    if (!inicio) {
        showError('inicio', 'Selecione a data e hora de início');
        isValid = false;
    }
    
    if (!fim) {
        showError('fim', 'Selecione a data e hora de término');
        isValid = false;
    }
    
    if (inicio && fim && new Date(fim) <= new Date(inicio)) {
        showError('fim', 'O término deve ser após o início');
        isValid = false;
    }
    
    if (!pessoas || parseInt(pessoas) < 1) {
        showError('pessoas', 'Número de pessoas deve ser pelo menos 1');
        isValid = false;
    }
    
    return isValid;
}

async function carregarMesas() {
    try {
        const res = await fetch(`${API}/api/mesas`, {
            headers: getAuthHeaders(),
            credentials: 'same-origin'
        });
        const mesas = await res.json();
        const select = document.getElementById('select-mesa');
        select.innerHTML = '<option value="">Selecione uma mesa</option>';
        mesas.forEach(mesa => {
            select.innerHTML += `<option value="${mesa.id}">Mesa ${mesa.numero} - ${mesa.localizacao} (Capacidade: ${mesa.capacidade})</option>`;
        });
    } catch (e) {
        console.error('Erro ao carregar mesas:', e);
    }
}

async function carregarMinhasReservas() {
    try {
        const res = await fetch(`${API}/api/reservas/minhas`, {
            headers: getAuthHeaders(),
            credentials: 'same-origin'
        });
        const reservas = await res.json();
        const container = document.getElementById('minhas-reservas');
        
        if (reservas.length === 0) {
            container.innerHTML = '<p class="text-center" style="width: 100%; color: #666; padding: 2rem;">Você não tem reservas ainda.</p>';
            return;
        }

        container.innerHTML = reservas.map(r => {
            const statusClass = r.status.toLowerCase();
            return `
                <div class="reserva-card">
                    <h4>Reserva #${r.id}</h4>
                    <p><strong>Mesa:</strong> Mesa ${r.mesaNumero} - ${r.mesaLocalizacao}</p>
                    <p><strong>Data e Hora:</strong> ${new Date(r.dataHoraInicio).toLocaleString('pt-BR')} até ${new Date(r.dataHoraFim).toLocaleString('pt-BR')}</p>
                    <p><strong>Pessoas:</strong> ${r.numeroPessoas}</p>
                    <span class="reserva-status ${statusClass}">${r.status}</span>
                    ${r.status === 'PENDENTE' || r.status === 'CONFIRMADA' 
                        ? `<button class="btn-action secondary" onclick="cancelarReserva(${r.id})">Cancelar</button>` 
                        : ''}
                </div>
            `;
        }).join('');
    } catch (e) {
        document.getElementById('minhas-reservas').innerHTML = '<p class="text-center" style="width: 100%; color: #dc3545; padding: 2rem;">Erro ao carregar reservas.</p>';
    }
}

// Criar reserva
async function criarReserva() {
    if (!validateForm()) {
        return;
    }

    const mesaId = document.getElementById('select-mesa').value;
    const dataHoraInicio = document.getElementById('data-inicio').value;
    const dataHoraFim = document.getElementById('data-fim').value;
    const numeroPessoas = document.getElementById('numero-pessoas').value;
    const observacao = document.getElementById('observacao').value;

    try {
        const res = await fetch(`${API}/api/reservas`, {
            method: 'POST',
            headers: getAuthHeaders(),
            credentials: 'same-origin',
            body: JSON.stringify({
                mesaId: parseInt(mesaId),
                dataHoraInicio: new Date(dataHoraInicio).toISOString(),
                dataHoraFim: new Date(dataHoraFim).toISOString(),
                numeroPessoas: parseInt(numeroPessoas),
                observacao: observacao
            })
        });

        if (res.ok) {
            // Show success message
            document.getElementById('form-success').classList.remove('hidden');
            // Clear form
            document.getElementById('select-mesa').value = '';
            document.getElementById('data-inicio').value = '';
            document.getElementById('data-fim').value = '';
            document.getElementById('numero-pessoas').value = '1';
            document.getElementById('observacao').value = '';
            // Hide success message after 5 seconds
            setTimeout(() => {
                document.getElementById('form-success').classList.add('hidden');
            }, 5000);
            // Refresh reservations list
            carregarMinhasReservas();
        } else {
            const erro = await res.json();
            showError('mesa', erro.erro || 'Erro ao criar reserva');
        }
    } catch (e) {
        showError('mesa', 'Erro ao conectar com o servidor');
    }
}

async function cancelarReserva(reservaId) {
    if (!confirm('Tem certeza que deseja cancelar esta reserva?')) {
        return;
    }

    try {
        const res = await fetch(`${API}/api/reservas/${reservaId}/cancelar`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            credentials: 'same-origin'
        });

        if (res.ok) {
            carregarMinhasReservas();
        } else {
            const erro = await res.json();
            alert(erro.erro || 'Erro ao cancelar reserva');
        }
    } catch (e) {
        alert('Erro ao conectar com o servidor');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    handleScrollAnimations();
    carregarMesas();
    carregarMinhasReservas();
});
