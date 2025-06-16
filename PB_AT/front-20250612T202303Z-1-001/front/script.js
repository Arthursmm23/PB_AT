
const API_URL = 'http://localhost:8080';

function showSection(sectionId) {
    document.querySelectorAll('main > section').forEach(section => {
        section.classList.add('hidden');
    });
    document.getElementById(sectionId)?.classList.remove('hidden');
    document.getElementById('nav')?.classList.toggle('hidden', sectionId === 'login' || sectionId === 'register');

    if (sectionId === 'veiculos') listVeiculos();
    if (sectionId === 'historico') document.getElementById('historicoList').innerHTML = '';
}

function showModal(title, message) {
    const modal = document.getElementById('modal');
    if (modal) {
        document.getElementById('modalTitle').textContent = title;
        document.getElementById('modalMessage').textContent = message;
        modal.classList.remove('hidden');
        modal.classList.add('flex');
    }
}

function closeModal() {
    const modal = document.getElementById('modal');
    if (modal) modal.classList.add('hidden');
}

function getToken() {
    return localStorage.getItem('token');
}

function getHeaders() {
    const token = getToken();
    return {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` })
    };
}

async function login() {
    const email = document.getElementById('loginEmail').value;
    const senha = document.getElementById('loginSenha').value;

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.mensagem || 'Erro ao fazer login');
        }

        const data = await response.json();
        localStorage.setItem('token', data.token);
        showSection('veiculos');
    } catch (error) {
        console.error('Erro no login:', error);
        showModal('Erro', error.message || 'Falha ao conectar com o servidor');
    }
}

async function register() {
    const nome = document.getElementById('registerNome').value;
    const email = document.getElementById('registerEmail').value;
    const senha = document.getElementById('registerSenha').value;
    const role = document.getElementById('registerRole').value;

    try {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome, email, senha, role })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.mensagem || 'Erro ao registrar');
        }

        showModal('Sucesso', 'Usuário registrado com sucesso! Faça login.');
        showSection('login');
    } catch (error) {
        console.error('Erro no registro:', error);
        showModal('Erro', error.message || 'Falha ao conectar com o servidor');
    }
}

function logout() {
    localStorage.removeItem('token');
    showSection('login');
}

async function listVeiculos() {
    try {
        const response = await fetch(`${API_URL}/veiculos?page=0&size=10`, {
            headers: getHeaders()
        });

        if (response.status === 401) throw new Error('Sessão expirada. Faça login novamente.');
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao listar veículos');
        }

        const data = await response.json();
        const veiculos = data.content || data;
        renderVeiculos(veiculos);
        window.veiculosList = veiculos;
    } catch (error) {
        console.error('Erro ao listar veículos:', error);
        if (error.message.includes('Sessão expirada')) logout();
        showModal('Erro', error.message || 'Falha ao listar veículos');
    }
}

function renderVeiculos(veiculos) {
    const veiculosList = document.getElementById('veiculosList');
    if (!veiculosList) return;

    veiculosList.innerHTML = '';

    if (!veiculos || veiculos.length === 0) {
        veiculosList.innerHTML = '<p class="text-gray-500 py-4 text-center">Nenhum veículo cadastrado</p>';
        return;
    }

    veiculos.forEach(veiculo => {
        const div = document.createElement('div');
        div.className = 'bg-white p-4 rounded shadow mb-4';
        div.innerHTML = `
            <h3 class="text-lg font-bold">${veiculo.modelo || 'Sem modelo'} (${veiculo.placa || 'Sem placa'})</h3>
            <p class="mt-1"><span class="font-semibold">Tipo:</span> ${veiculo.tipo || 'Não informado'}</p>
            <p class="mt-1"><span class="font-semibold">Ano:</span> ${veiculo.ano || 'Não informado'}</p>
            <p class="mt-1"><span class="font-semibold">Quilometragem:</span> ${veiculo.quilometragemAtual || '0'}</p>
            <div class="mt-3 flex flex-wrap gap-2">
                <button onclick="showEditVeiculoForm('${veiculo.placa}')" class="bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded">Editar</button>
                <button onclick="deleteVeiculo('${veiculo.placa}')" class="bg-red-600 hover:bg-red-700 text-white px-3 py-1 rounded">Excluir</button>
                <button onclick="showAddManutencaoForm('${veiculo.placa}')" class="bg-green-600 hover:bg-green-700 text-white px-3 py-1 rounded">Manutenção</button>
            </div>`;
        veiculosList.appendChild(div);
    });
}

function showAddVeiculoForm() {
    document.getElementById('veiculoFormTitle').textContent = 'Adicionar Veículo';
    document.getElementById('veiculoPlaca').value = '';
    document.getElementById('veiculoPlacaInput').value = '';
    document.getElementById('veiculoPlacaInput').disabled = false;
    document.getElementById('veiculoModelo').value = '';
    document.getElementById('veiculoAno').value = '';
    document.getElementById('veiculoQuilometragem').value = '';
    document.getElementById('veiculoEmail').value = '';
    showSection('veiculoForm');
}

async function showEditVeiculoForm(placa) {
    try {
        const veiculo = window.veiculosList?.find(v => v.placa === placa);
        if (!veiculo) throw new Error('Veículo não encontrado');

        document.getElementById('veiculoFormTitle').textContent = 'Editar Veículo';
        document.getElementById('veiculoPlaca').value = veiculo.placa;
        document.getElementById('veiculoPlacaInput').value = veiculo.placa;
        document.getElementById('veiculoPlacaInput').disabled = true;
        document.getElementById('veiculoModelo').value = veiculo.modelo;
        document.getElementById('veiculoAno').value = veiculo.ano;
        document.getElementById('veiculoQuilometragem').value = veiculo.quilometragemAtual;
        showSection('veiculoForm');
    } catch (error) {
        console.error('Erro ao carregar veículo:', error);
        showModal('Erro', error.message || 'Falha ao carregar veículo');
    }
}

async function saveVeiculo() {
    const placa = document.getElementById('veiculoPlaca').value;
   const veiculo = {
    placa: document.getElementById('veiculoPlacaInput').value,
    modelo: document.getElementById('veiculoModelo').value,
    tipo: document.getElementById('veiculoTipo').value,
    ano: parseInt(document.getElementById('veiculoAno').value),
    quilometragemAtual: parseInt(document.getElementById('veiculoQuilometragem').value),
    usuarioEmail: document.getElementById('veiculoEmail').value
};

    const method = placa ? 'PUT' : 'POST';
    const url = placa ? `${API_URL}/veiculos/${placa}` : `${API_URL}/veiculos`;

    try {
        const response = await fetch(url, {
            method,
            headers: getHeaders(),
            body: JSON.stringify(veiculo)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.mensagem || 'Erro ao salvar veículo');
        }

        showModal('Sucesso', `Veículo ${placa ? 'atualizado' : 'adicionado'} com sucesso`);
        showSection('veiculos');
    } catch (error) {
        console.error('Erro ao salvar veículo:', error);
        showModal('Erro', error.message || 'Falha ao conectar com o servidor');
    }
}

async function deleteVeiculo(placa) {
    if (!confirm('Tem certeza que deseja excluir este veículo?')) return;

    try {
        const response = await fetch(`${API_URL}/veiculos/${placa}`, {
            method: 'DELETE',
            headers: getHeaders()
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.mensagem || 'Erro ao excluir veículo');
        }

        showModal('Sucesso', 'Veículo excluído com sucesso');
        listVeiculos();
    } catch (error) {
        console.error('Erro ao excluir veículo:', error);
        showModal('Erro', error.message || 'Falha ao conectar com o servidor');
    }
}

function showAddManutencaoForm(placa) {
    document.getElementById('manutencaoPlaca').value = placa;
    document.getElementById('manutencaoTipo').value = '';
    document.getElementById('manutencaoData').value = '';
    document.getElementById('manutencaoQuilometragem').value = '';
    document.getElementById('manutencaoCusto').value = '';
    showSection('manutencaoForm');
}

async function saveManutencao() {
    const placa = document.getElementById('manutencaoPlaca').value;
    const manutencao = {
        tipo: document.getElementById('manutencaoTipo').value,
        data: document.getElementById('manutencaoData').value,
        quilometragem: parseInt(document.getElementById('manutencaoQuilometragem').value),
        custo: parseFloat(document.getElementById('manutencaoCusto').value)
    };

    try {
        const response = await fetch(`${API_URL}/veiculos/${placa}/manutencoes`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(manutencao)
        });

        if (!response.ok) {
            const text = await response.text();
            try {
                const error = JSON.parse(text);
                throw new Error(error.mensagem || 'Erro ao adicionar manutenção');
            } catch {
                throw new Error(text || 'Erro desconhecido ao adicionar manutenção');
            }
        }

        showModal('Sucesso', 'Manutenção adicionada com sucesso');
        showSection('veiculos');
    } catch (error) {
        console.error('Erro ao salvar manutenção:', error);
        showModal('Erro', error.message || 'Falha ao conectar com o servidor');
    }
}

async function consultarHistorico() {
    const dataInicial = document.getElementById('historicoDataInicial').value;
    const dataFinal = document.getElementById('historicoDataFinal').value;

    try {
       const response = await fetch(`${API_URL}/historico?dataInicial=${dataInicial}&dataFinal=${dataFinal}`, {

            headers: getHeaders()
        });

        if (response.status === 401) throw new Error('Sessão expirada. Faça login novamente.');
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao consultar histórico');
        }

        const data = await response.json();
        renderHistorico(data);
    } catch (error) {
        console.error('Erro ao consultar histórico:', error);
        if (error.message.includes('Sessão expirada')) logout();
        showModal('Erro', error.message || 'Falha ao consultar histórico');
    }
}

function renderHistorico(historico) {
    const historicoList = document.getElementById('historicoList');
    if (!historicoList) return;

    historicoList.innerHTML = '';

    if (!historico || historico.length === 0) {
        historicoList.innerHTML = '<p class="text-gray-500 py-4 text-center">Nenhum registro encontrado</p>';
        return;
    }

    historico.forEach(item => {
        const div = document.createElement('div');
        div.className = 'bg-white p-4 rounded shadow mb-4';
        div.innerHTML = `
            <p><span class="font-semibold">Data:</span> ${new Date(item.dataHora).toLocaleString()}</p>
            <p class="mt-1"><span class="font-semibold">Ação:</span> ${item.acao}</p>
            <p class="mt-1"><span class="font-semibold">Placa:</span> ${item.placa}</p>
        `;
        historicoList.appendChild(div);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    if (getToken()) {
        showSection('veiculos');
    } else {
        showSection('login');
    }
});


function renderTipoVeiculoChart() {
    const tipos = { Combustao: 0, Eletrico: 0, Hibrido: 0 };

    (window.veiculosList || []).forEach(v => {
        if (tipos.hasOwnProperty(v.tipo)) {
            tipos[v.tipo]++;
        }
    });

    const data = {
        labels: ['Combustão', 'Elétrico', 'Híbrido'],
        datasets: [{
            label: 'Tipos de Veículos',
            data: [tipos.Combustao, tipos.Eletrico, tipos.Hibrido],
            backgroundColor: ['#4B5563', '#10B981', '#F59E0B'],
            borderWidth: 1
        }]
    };

    const config = {
        type: 'pie',
        data: data,
        options: {
            responsive: true,
            plugins: {
                legend: { position: 'bottom' },
                title: { display: true, text: 'Distribuição de Tipos de Veículos' }
            }
        }
    };

    const canvas = document.getElementById('tipoVeiculoChart');
    if (canvas.chart) canvas.chart.destroy();
    canvas.classList.remove('hidden');
    canvas.chart = new Chart(canvas, config);
}
