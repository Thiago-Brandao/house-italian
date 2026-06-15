const API = 'http://localhost:8080';
let mesaIdEditar = null;

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

function atualizarNavbar() {
    document.getElementById('nav-nome').textContent = getNome();
}

function mostrarTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    
    document.getElementById('tab-' + tabId).classList.add('active');
    event.target.classList.add('active');

    if (tabId === 'mesas') carregarMesasAdmin();
    if (tabId === 'reservas') carregarReservasAdmin();
    if (tabId === 'usuarios') carregarUsuariosAdmin();
    if (tabId === 'auditoria') carregarAuditoriaAdmin();
}

// ==================== MESAS ====================
async function carregarMesasAdmin() {
    try {
        const res = await fetch(`${API}/api/mesas?todas=true`, {
            headers: getAuthHeaders(),
            credentials: 'same-origin'
        });
        const mesas = await res.json();
        const container = document.getElementById('lista-mesas');
        
        container.innerHTML = mesas.map(m => `
            <div class="mesa-item">
                <h4>Mesa ${m.numero}</h4>
                <p>Localização: ${m.localizacao}</p>
                <p>Capacidade: ${m.capacidade}</p>
                <p>Status: ${m.ativa ? 'Ativa' : 'Inativa'}</p>
                <button onclick="editarMesa(${m.id})">Editar</button>
                <button onclick="alterarStatusMesa(${m.id}, ${!m.ativa})">
                    ${m.ativa ? 'Inativar' : 'Ativar'}
                </button>
            </div>
        `).join('');
    } catch (e) {
        document.getElementById('lista-mesas').innerHTML = '<p>Erro ao carregar mesas.</p>';
    }
}

function abrirModalMesa(mesa = null) {
    mesaIdEditar = mesa ? mesa.id : null;
    document.getElementById('modal-titulo-mesa').textContent = mesa ? 'Editar Mesa' : 'Adicionar Mesa';
    document.getElementById('modal-mesa-numero').value = mesa ? mesa.numero : '';
    document.getElementById('modal-mesa-capacidade').value = mesa ? mesa.capacidade : '';
    document.getElementById('modal-mesa-localizacao').value = mesa ? mesa.localizacao : '';
    document.getElementById('modal-mesa-descricao').value = mesa ? mesa.descricao : '';
    document.getElementById('modal-mesa').classList.remove('hidden');
}

function fecharModalMesa() {
    document.getElementById('modal-mesa').classList.add('hidden');
}

async function salvarMesa() {
    const numero = parseInt(document.getElementById('modal-mesa-numero').value);
    const capacidade = parseInt(document.getElementById('modal-mesa-capacidade').value);
    const localizacao = document.getElementById('modal-mesa-localizacao').value;
    const descricao = document.getElementById('modal-mesa-descricao').value;

    try {
        const url = mesaIdEditar 
            ? `${API}/api/mesas/${mesaIdEditar}`
            : `${API}/api/mesas`;
        const method = mesaIdEditar ? 'PUT' : 'POST';

        const res = await fetch(url, {
            method: method,
            headers: getAuthHeaders(),
            credentials: 'same-origin',
            body: JSON.stringify({ numero, capacidade, localizacao, descricao })
        });

        if (res.ok) {
            alert('Mesa salva com sucesso!');
            fecharModalMesa();
            carregarMesasAdmin();
        } else {
            const erro = await res.json();
            alert(erro.erro || 'Erro ao salvar mesa');
        }
    } catch (e) {
        alert('Erro ao conectar com o servidor');
    }
}

async function editarMesa(id) {
    try {
        const res = await fetch(`${API}/api/mesas/${id}`, {
            headers: getAuthHeaders()
        });
        const mesa = await res.json();
        abrirModalMesa(mesa);
    } catch (e) {
        alert('Erro ao carregar mesa');
    }
}

async function alterarStatusMesa(id, ativa) {
    try {
        const res = await fetch(`${API}/api/mesas/${id}/status?ativa=${ativa}`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });

        if (res.ok) {
            alert('Status da mesa alterado com sucesso!');
            carregarMesasAdmin();
        } else {
            const erro = await res.json();
            alert(erro.erro || 'Erro ao alterar status');
        }
    } catch (e) {
        alert('Erro ao conectar com o servidor');
    }
}

// ==================== RESERVAS ====================
async function carregarReservasAdmin() {
    try {
        const res = await fetch(`${API}/api/reservas`, {
            headers: getAuthHeaders()
        });
        const reservas = await res.json();
        const container = document.getElementById('lista-reservas-admin');
        
        container.innerHTML = reservas.map(r => `
            <div class="reserva-item">
                <h4>Reserva #${r.id}</h4>
                <p>Cliente: ${r.usuarioNome}</p>
                <p>Mesa: ${r.mesaNumero}</p>
                <p>Data: ${new Date(r.dataHoraInicio).toLocaleString('pt-BR')}</p>
                <p>Status: ${r.status}</p>
                ${r.status === 'PENDENTE' 
                    ? `<button onclick="confirmarReserva(${r.id})">Confirmar</button>` 
                    : ''}
                ${r.status === 'CONFIRMADA' 
                    ? `<button onclick="concluirReserva(${r.id})">Concluir</button>` 
                    : ''}
            </div>
        `).join('');
    } catch (e) {
        document.getElementById('lista-reservas-admin').innerHTML = '<p>Erro ao carregar reservas.</p>';
    }
}

async function confirmarReserva(id) {
    try {
        const res = await fetch(`${API}/api/reservas/${id}/confirmar`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });

        if (res.ok) {
            alert('Reserva confirmada!');
            carregarReservasAdmin();
        } else {
            const erro = await res.json();
            alert(erro.erro || 'Erro ao confirmar reserva');
        }
    } catch (e) {
        alert('Erro ao conectar com o servidor');
    }
}

async function concluirReserva(id) {
    try {
        const res = await fetch(`${API}/api/reservas/${id}/concluir`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });

        if (res.ok) {
            alert('Reserva concluída!');
            carregarReservasAdmin();
        } else {
            const erro = await res.json();
            alert(erro.erro || 'Erro ao concluir reserva');
        }
    } catch (e) {
        alert('Erro ao conectar com o servidor');
    }
}

// ==================== USUÁRIOS ====================
async function carregarUsuariosAdmin() {
    try {
        const res = await fetch(`${API}/api/usuarios`, {
            headers: getAuthHeaders()
        });
        const usuarios = await res.json();
        const container = document.getElementById('lista-usuarios');
        
        container.innerHTML = usuarios.map(u => `
            <div class="usuario-item">
                <h4>${u.nome}</h4>
                <p>Email: ${u.email}</p>
                <p>Perfil: ${u.perfil}</p>
                <p>Status: ${u.ativo ? 'Ativo' : 'Inativo'}</p>
                <button onclick="alterarStatusUsuario(${u.id}, ${!u.ativo})">
                    ${u.ativo ? 'Inativar' : 'Ativar'}
                </button>
            </div>
        `).join('');
    } catch (e) {
        document.getElementById('lista-usuarios').innerHTML = '<p>Erro ao carregar usuários.</p>';
    }
}

async function alterarStatusUsuario(id, ativo) {
    try {
        const res = await fetch(`${API}/api/usuarios/${id}/status?ativo=${ativo}`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });

        if (res.ok) {
            alert('Status do usuário alterado com sucesso!');
            carregarUsuariosAdmin();
        } else {
            const erro = await res.json();
            alert(erro.erro || 'Erro ao alterar status');
        }
    } catch (e) {
        alert('Erro ao conectar com o servidor');
    }
}

// ==================== AUDITORIA ====================
async function carregarAuditoriaAdmin() {
    try {
        const res = await fetch(`${API}/api/auditoria`, {
            headers: getAuthHeaders()
        });
        const logs = await res.json();
        const container = document.getElementById('lista-auditoria');
        
        container.innerHTML = logs.map(log => `
            <div class="log-item">
                <h4>${log.acao}</h4>
                <p>Entidade: ${log.entidade} (ID: ${log.entidadeId})</p>
                <p>Usuário: ${log.usuarioNome} (${log.usuarioEmail})</p>
                <p>Data: ${new Date(log.dataHora).toLocaleString('pt-BR')}</p>
                <p>Detalhes: ${log.detalhes}</p>
            </div>
        `).join('');
    } catch (e) {
        document.getElementById('lista-auditoria').innerHTML = '<p>Erro ao carregar logs.</p>';
    }
}

atualizarNavbar();
carregarMesasAdmin();
