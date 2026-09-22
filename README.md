# MundoWumpusDia7

Sétima versão didática. O agente automático usa regras simples para combinar
memória, brisa, fedor, exploração, estimativa de risco e retorno planejado.

## Novidade do Dia 7

- cada coordenada possui uma quantidade de visitas e um nível de risco;
- brisa e fedor aumentam o risco de vizinhos ainda desconhecidos;
- casas novas recebem bônus na avaliação;
- risco e repetição reduzem a nota de uma opção;
- o agente escolhe uma das direções com maior nota;
- ao sentir fedor, usa a flecha contra uma hipótese de posição do Wumpus.
- o caminho conhecido é registrado em uma lista ordenada;
- voltas repetidas são retiradas desse caminho;
- depois de pegar o ouro, o agente refaz o caminho ao contrário até `[0][0]`.

A regra utilizada é exibida no terminal, incluindo direção, risco, número de
visitas e nota. Empates são resolvidos aleatoriamente. Como as percepções não
identificam exatamente a casa perigosa, o agente ainda pode errar durante a
exploração.

## Dois modos de comportamento

```text
SEM OURO  -> explorar o mapa usando memória e estimativa de risco
COM OURO  -> retornar pelo caminho já percorrido até a casa inicial
```

O limite de 180 movimentos é aplicado somente à exploração. Se o ouro for
encontrado, o retorno não é interrompido pelo limite.

## Fórmula didática

```text
nota = bônus de casa nova - penalidade de risco - penalidade de repetição
```

No código: casa nova vale `+100`, cada nível de risco vale `-120` e cada visita
anterior vale `-5`.

## Como abrir no NetBeans

1. Extraia `MundoWumpusDia7.zip`.
2. Abra a pasta `MundoWumpusDia7` como projeto no NetBeans.
3. Pressione **F6**. A demonstração é automática.

## Classes

- `Main.java`: alterna entre os modos de exploração e retorno;
- `Mundo.java`: fornece mapa, percepções e trajetória da flecha;
- `AgenteInteligente.java`: mantém visitas, riscos, decisões e o caminho
  percorrido.
