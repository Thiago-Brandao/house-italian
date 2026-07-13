// Função para limpar erros
function limparErros() {
    document.querySelectorAll('.error-message, .form-error-message').forEach(el => el.textContent = '');
    document.querySelectorAll('input').forEach(el => el.classList.remove('invalid'));
}

// Função para mostrar erro em um campo
function mostrarErro(campo, mensagem) {
    const input = document.getElementById(`input-${campo}`);
    const errorSpan = document.getElementById(`error-${campo}`);
    
    if (input) input.classList.add('invalid');
    if (errorSpan) {
        errorSpan.textContent = mensagem;
    } else if (campo === 'form') {
        const formError = document.getElementById('error-form');
        if (formError) formError.textContent = mensagem;
    }
}

// Função para validar o formulário de login
function validarLogin() {
    limparErros();
    let valido = true;
    
    const email = document.getElementById('input-email').value.trim();
    const senha = document.getElementById('input-senha').value.trim();
    
    // Validação de email
    if (!email) {
        mostrarErro('email', 'O email é obrigatório.');
        valido = false;
    } else {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            mostrarErro('email', 'Por favor, insira um email válido.');
            valido = false;
        }
    }
    
    // Validação de senha
    if (!senha) {
        mostrarErro('senha', 'A senha é obrigatória.');
        valido = false;
    } else if (senha.length < 8) {
        mostrarErro('senha', 'A senha deve ter pelo menos 8 caracteres.');
        valido = false;
    }
    
    return valido;
}

// Adiciona listeners para limpar erro quando o usuário começa a digitar
document.getElementById('input-email')?.addEventListener('input', () => {
    document.getElementById('error-email').textContent = '';
    document.getElementById('error-form').textContent = '';
    document.getElementById('input-email').classList.remove('invalid');
});

document.getElementById('input-email')?.addEventListener('blur', () => {
    const email = document.getElementById('input-email').value.trim();
    if (email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            mostrarErro('email', 'Por favor, insira um email válido.');
        }
    }
});

document.getElementById('input-senha')?.addEventListener('input', () => {
    document.getElementById('error-senha').textContent = '';
    document.getElementById('error-form').textContent = '';
    document.getElementById('input-senha').classList.remove('invalid');
});

async function fazerLogin() {
    if (!validarLogin()) return;

    const email = document.getElementById('input-email').value;
    const senha = document.getElementById('input-senha').value;

    try {
        const resposta = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin', // Necessário para receber o cookie
            body: JSON.stringify({ email, senha })
        });

        const dados = await resposta.json();

        if (resposta.ok) {
            localStorage.setItem('token', dados.token);
            localStorage.setItem('perfil', dados.perfil);
            localStorage.setItem('nome', dados.nome);

            const redirectUrl = localStorage.getItem('redirect');
            localStorage.removeItem('redirect');

            if (redirectUrl) {
                window.location.href = redirectUrl;
            } else if (dados.perfil === 'ADMIN') {
                window.location.href = '/dashboard';
            } else {
                window.location.href = '/reserva';
            }
        } else {
            mostrarErro('form', dados.erro || 'E-mail ou senha inválidos');
        }
    } catch (e) {
        mostrarErro('form', 'Erro ao conectar com o servidor.');
    }
}
