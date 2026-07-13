const API = 'http://localhost:8080';

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

// Função para validar o formulário de cadastro
function validarCadastro() {
    limparErros();
    let valido = true;
    
    const nome = document.getElementById('input-nome').value.trim();
    const email = document.getElementById('input-email').value.trim();
    const senha = document.getElementById('input-senha').value;
    const confirmarSenha = document.getElementById('input-confirmar-senha').value;
    
    // Validação de nome
    if (!nome) {
        mostrarErro('nome', 'O nome é obrigatório.');
        valido = false;
    } else if (nome.length < 2) {
        mostrarErro('nome', 'O nome deve ter pelo menos 2 caracteres.');
        valido = false;
    }
    
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
    
    // Validação de confirmar senha
    if (!confirmarSenha) {
        mostrarErro('confirmar-senha', 'Por favor, confirme sua senha.');
        valido = false;
    } else if (confirmarSenha !== senha) {
        mostrarErro('confirmar-senha', 'As senhas não coincidem.');
        valido = false;
    }
    
    return valido;
}

// Adiciona listeners para limpar erro quando o usuário começa a digitar
document.getElementById('input-nome')?.addEventListener('input', () => {
    document.getElementById('error-nome').textContent = '';
    document.getElementById('input-nome').classList.remove('invalid');
});

document.getElementById('input-nome')?.addEventListener('blur', () => {
    const nome = document.getElementById('input-nome').value.trim();
    if (nome && nome.length < 2) {
        mostrarErro('nome', 'O nome deve ter pelo menos 2 caracteres.');
    }
});

document.getElementById('input-email')?.addEventListener('input', () => {
    document.getElementById('error-email').textContent = '';
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
    document.getElementById('input-senha').classList.remove('invalid');
});

document.getElementById('input-confirmar-senha')?.addEventListener('input', () => {
    document.getElementById('error-confirmar-senha').textContent = '';
    document.getElementById('input-confirmar-senha').classList.remove('invalid');
});

async function fazerCadastro() {
    if (!validarCadastro()) return;
    
    const nome = document.getElementById('input-nome').value.trim();
    const email = document.getElementById('input-email').value.trim();
    const senha = document.getElementById('input-senha').value;

    try {
        const resposta = await fetch(`${API}/api/usuarios`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin',
            body: JSON.stringify({ nome, email, senha })
        });

        const dados = await resposta.json();

        if (resposta.ok) {
            const formSuccess = document.getElementById('error-form');
            if (formSuccess) {
                formSuccess.textContent = 'Cadastro realizado com sucesso! Redirecionando para login...';
                formSuccess.style.color = '#1f5c1f';
            }
            setTimeout(() => window.location.href = '/login', 1200);
        } else {
            limparErros();
            if (dados.erro?.toLowerCase().includes('email')) {
                mostrarErro('email', dados.erro);
            } else {
                mostrarErro('form', dados.erro || 'Erro ao fazer cadastro');
            }
        }
    } catch (e) {
        limparErros();
        mostrarErro('form', 'Erro ao conectar com o servidor');
    }
}
