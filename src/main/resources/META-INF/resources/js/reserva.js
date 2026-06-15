const API = 'http://localhost:8080';

function getToken()  { return localStorage.getItem('token'); }
function getPerfil() { return localStorage.getItem('perfil'); }
function getNome()   { return localStorage.getItem('nome'); }

function getAuthHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + getToken()
    };
}

function logout() {
    fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'same-origin' // Necessário para enviar/remover cookies
    })
    .then(() => {
        localStorage.clear();
        window.location.href = '/'; // Redireciona para a página inicial
    })
    .catch((erro) => {
        console.error('Erro ao fazer logout:', erro);
        // Mesmo com erro, limpa o localStorage
        localStorage.clear();
        window.location.href = '/';
    });
}

function irParaDashboard() {
    window.location.href = getPerfil() === 'ADMIN'
        ? '/dashboard' : '/reserva';
}

function atualizarNavbar() {
    document.getElementById('nav-nome').textContent = getNome();
}

async function carregarMesas() {
    try {
        const res = await fetch(`${API}/api/mesas`, {
            headers: getAuthHeaders()
        });
        const mesas = await res.json();
        const select = document.getElementById('select-mesa');
        select.innerHTML = '<option value="">Selecione uma mesa</option>';
        mesas.forEach(mesa => {
            select.innerHTML += `<option value="${mesa.id}">Mesa ${mesa.numero} - ${mesa.localizacao} (Capacidade: ${mesa.capacidade})</option>`;
        });
    } catch (e) {
        alert('Erro ao carregar mesas');
    }
}

async function carregarMinhasReservas() {
    try {
        const res = await fetch(`${API}/api/reservas/minhas`, {
            headers: getAuthHeaders()
        });
        const reservas = await res.json();
        const container = document.getElementById('minhas-reservas');
        
        if (reservas.length === 0) {
            container.innerHTML = '<p>Você não tem reservas ainda.</p>';
            return;
        }

        container.innerHTML = reservas.map(r => `
            <div class="reserva-item">
                <h3>Reserva #${r.id}</h3>
                <p>Mesa: ${r.mesaNumero} - ${r.mesaLocalizacao}</p>
                <p>Data: ${new Date(r.dataHoraInicio).toLocaleString('pt-BR')} até ${new Date(r.dataHoraFim).toLocaleString('pt-BR')}</p>
                <p>Pessoas: ${r.numeroPessoas}</p>
                <p>Status: ${r.status}</p>
                ${r.status === 'PENDENTE' || r.status === 'CONFIRMADA' 
                    ? `<button onclick="cancelarReserva(${r.id})">Cancelar</button>` 
                    : ''}
            </div>
        `).join('');
    } catch (e) {
        document.getElementById('minhas-reservas').innerHTML = '<p>Erro ao carregar reservas.</p>';
    }
}

async function criarReserva() {
    const mesaId = document.getElementById('select-mesa').value;
    const dataHoraInicio = document.getElementById('data-inicio').value;
    const dataHoraFim = document.getElementById('data-fim').value;
    const numeroPessoas = document.getElementById('numero-pessoas').value;
    const observacao = document.getElementById('observacao').value;

    if (!mesaId || !dataHoraInicio || !dataHoraFim || !numeroPessoas) {
        alert('Preencha todos os campos obrigatórios!');
        return;
    }

    try {
        const res = await fetch(`${API}/api/reservas`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({
                mesaId: parseInt(mesaId),
                dataHoraInicio: new Date(dataHoraInicio).toISOString(),
                dataHoraFim: new Date(dataHoraFim).toISOString(),
                numeroPessoas: parseInt(numeroPessoas),
                observacao: observacao
            })
        });

        if (res.ok) {
            alert('Reserva criada com sucesso!');
            carregarMinhasReservas();
        } else {
            const erro = await res.json();
            alert(erro.erro || 'Erro ao criar reserva');
        }
    } catch (e) {
        alert('Erro ao conectar com o servidor');
    }
}

async function cancelarReserva(reservaId) {
    if (!confirm('Tem certeza que deseja cancelar esta reserva?')) {
        return;
    }

    try {
        const res = await fetch(`${API}/api/reservas/${reservaId}/cancelar`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });

        if (res.ok) {
            alert('Reserva cancelada com sucesso!');
            carregarMinhasReservas();
        } else {
            const erro = await res.json();
            alert(erro.erro || 'Erro ao cancelar reserva');
        }
    } catch (e) {
        alert('Erro ao conectar com o servidor');
    }
}

atualizarNavbar();
carregarMesas();
carregarMinhasReservas();
