# AIAD1819

Coisas que acho que faltam:

  - Quando o carro muda de rua, não está a atualizar bem o carro da frente e tras, porque entra a meio;
  A ideia era mandar mensagem a todos os carros dessa rua a perguntar onde estão, e depois determinar a posição do carro na lista, ou seja reordenar a lista.
  - O único algoritmo de seleçao de carros numa interseçao é um de cada vez por ordem de chegada, é preciso acrescentar collision detection e assim;
  - As ruas estao definidas no Map.java, acho que dava para fazer um ficheiro tipo .xml e carregar para as estruturas de dados, mas não é muito prioritario;
  - O caminho dos carros está definido em array com ruas tipo [1,3,5], acho que era fixe implementar o ponto de partida e chegada e o carro calculava o caminho com A* ou assim, atraves das ruas das interseçoes.
  - O car spawner está a criar carros em locais onde estão carros. Era preciso algum controlo do local inicial, e também ter em conta o numero de carros atualmente na rua.
  
 
 
 Mapa utilizado, com ruas numeradas:
  
             |   |    |              |   |    |
             | 7 |  8 |              |11 |  12| 
    _________|   |    |______________|   |    |_________
      1                     3                      5
    __________         _____________           ___________
      2                     4                      6
    __________         _____________           ___________
             |9  |10  |              |13 |  14| 
             |   |    |              |   |    | 
             |   |    |              |   |    |
