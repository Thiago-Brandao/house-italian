const API = 'http://localhost:8080';

async function fazerCadastro() {
    const nome = document.getElementById('input-nome').value;
    const email = document.getElementById('input-email').value;
    const senha = document.getElementById('input-senha').value;

    if (!nome || !email || !senha) {
        alert('Preencha todos os campos!');
        return;
    }

    if (senha.length < 8) {
        alert('A senha deve ter pelo menos 8 caracteres!');
        return;
    }

    try {
        const resposta = await fetch(`${API}/api/usuarios`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome, email, senha })
        });

        const dados = await resposta.json();

        if (resposta.ok) {
            alert('Cadastro realizado com sucesso! Faça login.');
            window.location.href = '/login';
        } else {
            alert(dados.erro || 'Erro ao fazer cadastro');
        }
    } catch (e) {
        alert('Erro ao conectar com o servidor');
    }
}
