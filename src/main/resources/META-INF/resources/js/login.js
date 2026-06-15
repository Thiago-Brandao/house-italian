async function fazerLogin() {
    const email = document.getElementById('input-email').value;
    const senha = document.getElementById('input-senha').value;

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
        alert(dados.erro || 'E-mail ou senha inválidos');
    }
}
