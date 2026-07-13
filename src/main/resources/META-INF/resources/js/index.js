function getToken()  { return localStorage.getItem('token'); }
function getPerfil() { return localStorage.getItem('perfil'); }
function getNome()   { return localStorage.getItem('nome'); }

function irParaReserva() {
    if (!getToken()) {
        localStorage.setItem('redirect', '/reserva');
        window.location.href = '/login';
    } else if (getPerfil() === 'ADMIN') {
        window.location.href = '/dashboard';
    } else {
        window.location.href = '/reserva';
    }
}

function logout() {
    fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'same-origin'
    })
    .then(() => {
        localStorage.clear();
        atualizarNavbar();
        window.location.href = '/';
    })
    .catch((erro) => {
        console.error('Erro ao fazer logout:', erro);
        localStorage.clear();
        atualizarNavbar();
        window.location.href = '/';
    });
}

function atualizarNavbar() {
    const btnEntrar = document.getElementById('navbar-user-logged-out');
    const navUsuario = document.getElementById('navbar-user-logged-in');
    const navNome = document.getElementById('nav-nome');
    const actionLink = document.getElementById('nav-action-link');

    if (getToken()) {
        if (btnEntrar) btnEntrar.classList.add('hidden');
        if (navUsuario) navUsuario.classList.remove('hidden');
        if (navNome) navNome.textContent = getNome();
    } else {
        if (btnEntrar) btnEntrar.classList.remove('hidden');
        if (navUsuario) navUsuario.classList.add('hidden');
    }

    if (actionLink) {
        actionLink.textContent = getToken() && getPerfil() === 'ADMIN' ? 'Painel' : 'Reserva';
    }
}

document.addEventListener('DOMContentLoaded', function() {
    atualizarNavbar();
});
