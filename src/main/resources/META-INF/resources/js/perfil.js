const API = 'http://localhost:8080';

function getAuthHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + getToken()
    };
}

// Fade-in Animation on Scroll
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

// Get initials from name
function getInitials(name) {
    return name
        .split(' ')
        .map(n => n[0])
        .slice(0, 2)
        .join('')
        .toUpperCase();
}

async function carregarPerfil() {
    try {
        const res = await fetch(`${API}/api/usuarios/me`, {
            headers: getAuthHeaders(),
            credentials: 'same-origin'
        });
        const usuario = await res.json();
        
        // Set initials
        document.getElementById('perfil-initials').textContent = getInitials(usuario.nome);
        
        // Set info grid
        const grid = document.getElementById('perfil-info-grid');
        grid.innerHTML = `
            <div class="perfil-field">
                <div class="perfil-field-icon">
                    <i class="far fa-user"></i>
                </div>
                <div class="perfil-field-content">
                    <span class="perfil-field-label">Nome</span>
                    <span class="perfil-field-value">${usuario.nome}</span>
                </div>
            </div>
            <div class="perfil-field">
                <div class="perfil-field-icon">
                    <i class="far fa-envelope"></i>
                </div>
                <div class="perfil-field-content">
                    <span class="perfil-field-label">Email</span>
                    <span class="perfil-field-value">${usuario.email}</span>
                </div>
            </div>
            <div class="perfil-field">
                <div class="perfil-field-icon">
                    <i class="fas fa-shield-alt"></i>
                </div>
                <div class="perfil-field-content">
                    <span class="perfil-field-label">Perfil</span>
                    <span class="perfil-field-value">${usuario.perfil}</span>
                </div>
            </div>
            <div class="perfil-field">
                <div class="perfil-field-icon">
                    <i class="far fa-calendar-alt"></i>
                </div>
                <div class="perfil-field-content">
                    <span class="perfil-field-label">Data de Cadastro</span>
                    <span class="perfil-field-value">${new Date(usuario.dataCadastro).toLocaleDateString('pt-BR')}</span>
                </div>
            </div>
        `;
    } catch (e) {
        document.getElementById('perfil-info-grid').innerHTML = '<div class="text-center" style="grid-column: 1/-1; padding: 2rem; color: #dc3545;">Erro ao carregar perfil.</div>';
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
            container.innerHTML = `
                <div class="perfil-empty-state">
                    <div class="perfil-empty-icon">
                        <i class="far fa-calendar"></i>
                    </div>
                    <p>Você não tem reservas ainda.</p>
                    <small>Que tal reservar uma mesa para sua próxima visita?</small>
                    <a href="/reserva" class="btn-primary" style="padding: 0.75rem 1.5rem; font-size: 0.9rem;">Fazer Primeira Reserva</a>
                </div>
            `;
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
        document.getElementById('minhas-reservas').innerHTML = '<div class="text-center" style="grid-column: 1/-1; padding: 2rem; color: #dc3545;">Erro ao carregar reservas.</div>';
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
    carregarPerfil();
    carregarMinhasReservas();
});
