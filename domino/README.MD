🎲 DominoVerse - Jogo de Dominó Multiplayer em Java
Desenvolvido para a disciplina de Desenvolvimento Orientado a Objetos
Professor: Mário
Equipe:
✨ Rafael Matias | ✨ Thomas Segreto | ✨ Otávio Baroni

🌟 Visão Geral
DominoVerse é um jogo de dominó multiplayer desenvolvido em Java, onde dois jogadores podem se conectar via rede para desafiar um ao outro em partidas emocionantes!

Com uma arquitetura cliente-servidor robusta, o jogo segue as regras tradicionais do dominó, garantindo uma experiência autêntica e competitiva.

🎮 Funcionalidades Principais
✅ Modo Multiplayer Online – Conecte-se com um amigo e jogue em tempo real!
✅ Regras Clássicas do Dominó – Distribuição de peças, jogadas válidas e finalização correta.
✅ Sistema de Turnos Inteligente – Nunca perca sua vez!
✅ Compra de Peças Automática – Se não puder jogar, compre do monte!
✅ Detecção de Fim de Jogo – Vitória por peças zeradas ou menor pontuação em caso de "tranco".
✅ Reinício Automático – Jogue novamente sem sair do jogo!

⚙️ Tecnologias e Padrões Utilizados
Componente	Descrição
Java Socket	Comunicação cliente-servidor
Orientação a Objetos	Classes bem definidas (Pedra, MesaDeJogo, GerenciadorDeJogadas)
Threads	Processamento paralelo para múltiplos jogadores
Clean Code	Código organizado e documentado
🚀 Como Jogar?
1️⃣ Inicie o Servidor
bash
java -cp bin conexao.ServidorDomino
O servidor aguardará conexões dos jogadores.

2️⃣ Conecte os Jogadores
bash
java -cp bin jogo.JogoDomino
Cada jogador deve executar este comando em um terminal separado.

3️⃣ Comandos do Jogo
j → Jogar uma peça

p → Passar a vez (se não houver jogada possível)

l/r → Escolher lado da mesa (esquerda/direita)

🏆 Regras do Jogo
📌 Cada jogador começa com 7 peças.
📌 O jogador com a peça mais alta (6-6) inicia.
📌 Só é possível jogar nas extremidades da mesa.
📌 Se não puder jogar, compre peças do monte.
📌 Fim do jogo:

Um jogador zera suas peças → Vitória!

Jogo trancado (sem jogadas possíveis) → Quem tiver menos pontos vence!

📊 Diagrama de Funcionamento
text
         [Servidor]  
            ▲  ▲  
            │  │  
            ▼  ▼  
[Jogador 1]    [Jogador 2]  
Servidor controla a lógica do jogo.

Clientes enviam jogadas e recebem atualizações.

🛠 Próximas Atualizações (Roadmap)
🔹 Interface Gráfica (GUI) com JavaFX/Swing
🔹 Modo Singleplayer contra IA
🔹 Sistema de Ranking com histórico de partidas
🔹 Chat Integrado para comunicação entre jogadores

👨‍💻 Contribuições da Equipe
Integrante	Contribuição
Rafael Matias	Lógica do servidor e comunicação em rede
Thomas Segreto	Regras do jogo e gerenciamento da mesa
Otávio Baroni	Interface do cliente e validação de jogadas
🎉 Conclusão
DominoVerse é mais que um jogo – é um projeto que aplica boas práticas de programação, trabalho em equipe e lógica de jogos em um ambiente multiplayer.

Junte-se a nós e divirta-se com o clássico dominó em uma versão moderna e conectada!

🏁 Vamos jogar? 🚀

📌 Desenvolvido com ❤️ para a disciplina de DOO - UNIVALI