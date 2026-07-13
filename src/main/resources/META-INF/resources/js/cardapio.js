const API = 'http://localhost:8080';
let todosPratos = [];
let categoriaSelecionada = 'TODOS';

// imagens de pratos
const imagensPratos = {
    "Bruschetta Classica": "https://euamoonatal.com/wp-content/uploads/2025/10/como-preparar-a-bruschetta-classica-de-tomate-e-manjericao.jpg",
    "Caprese Salad": "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=600",
    "Lasagna alla Bolognese": "https://images.unsplash.com/photo-1619895092538-128341789043?w=600",
    "Fettuccine Alfredo": "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=600",
    "Gnocchi de Batata": "https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=600",
    "Spaghetti Carbonara": "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=600",
    "Polpetone Recheado": "https://www.receitasnestle.com.br/sites/default/files/styles/recipe_detail_desktop_new/public/srh_recipes/2bcbc59d1a2cb216f2ba3569043aaa91.jpg?itok=FzVYcxoK",
    "Osso Buco": "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600",
    "Tiramisu": "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=600",
    "Panna Cotta": "https://www.oetker.com.br/assets/recipes/assets/98c06c70a3cb4668aa901f0474fc5eb5/1272x764/panna-cotta-1.jpg",
    "Cannoli Siciliani": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSHbLYAexwaX7cKROG4QVtTFw6npCwm6nKFIs4QqBZeUP6pfWdCrkZdF48&s=10"
};

async function carregarCardapio() {
    try {
        const res = await fetch(`${API}/api/cardapio`);
        todosPratos = await res.json();
        exibirPratos();
    } catch (e) {
        console.error('Erro ao carregar cardápio:', e);
        document.getElementById('cardapio-grid').innerHTML =
            '<div class="text-center" style="width:100%; padding:2rem; color:#dc3545;">Erro ao carregar cardápio.</div>';
    }
}

function exibirPratos() {
    const grid = document.getElementById('cardapio-grid');
    const pratosFiltrados = categoriaSelecionada === 'TODOS' 
        ? todosPratos 
        : todosPratos.filter(p => p.categoria === categoriaSelecionada);

    if (pratosFiltrados.length === 0) {
        grid.innerHTML = '<div class="text-center" style="width:100%; padding:2rem; color:#666;">Nenhum prato disponível nesta categoria.</div>';
        return;
    }

    grid.innerHTML = pratosFiltrados.map(p => {
        const imagem = imagensPratos[p.nome] || "https://images.unsplash.com/photo-1619895092538-128341789043?w=600";
        return `
            <div class="prato-card">
                <div class="prato-img">
                    <img src="${imagem}" alt="${p.nome}">
                </div>
                <div class="prato-content">
                    <h3 class="prato-nome">${p.nome}</h3>
                    <p class="prato-desc">${p.descricao}</p>
                    <div class="prato-preco">R$ ${p.preco.toFixed(2).replace('.', ',')}</div>
                </div>
            </div>
        `;
    }).join('');
}

function configurarTabs() {
    const tabButtons = document.querySelectorAll('.cardapio-tab-btn');
    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            // Remove active from all buttons
            tabButtons.forEach(b => b.classList.remove('active'));
            // Add active to clicked button
            btn.classList.add('active');
            // Set selected category
            categoriaSelecionada = btn.dataset.categoria;
            // Refresh display
            exibirPratos();
        });
    });
}

document.addEventListener('DOMContentLoaded', () => {
    carregarCardapio();
    configurarTabs();
});
