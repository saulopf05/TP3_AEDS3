package menus;

import java.util.Scanner;
import java.util.ArrayList;

import arquivos.ArquivoPergunta;
import entidades.Pergunta;
import entidades.Usuario;
import indices.ParUsuarioPergunta;

public class MenuPerguntas {
    private static Scanner console = Console.entrada();
    private Usuario usuario;
    private ArquivoPergunta arqPerguntas;
    
    // Lista essencial para mapear a opção da tela (1, 2, 3...) para o ID real da Pergunta
    private ArrayList<Integer> mapPerguntas;

    public MenuPerguntas(Usuario usuario) throws Exception {
        this.usuario = usuario;
        this.arqPerguntas = new ArquivoPergunta();
        this.mapPerguntas = new ArrayList<>();
    }

    public void menu() {
        char opcao;

        do {
            System.out.println("\n\nMinhas perguntas");
            System.out.println("----------------");
            System.out.println("A - Listar");
            System.out.println("B - Incluir");
            System.out.println("C - Alterar");
            System.out.println("D - Arquivar");
            System.out.println("R - Retornar");

            System.out.print("\nOpcao: ");

            try {
                opcao = console.nextLine().toUpperCase().charAt(0);
            } catch(Exception e) {
                opcao = ' ';
            }

            switch(opcao) {
                case 'A':
                    listar();
                    break;
                case 'B':
                    incluir();
                    break;
                case 'C':
                    alterar();
                    break;
                case 'D':
                    arquivar();
                    break;
                case 'R':
                    break;
                default:
                    System.out.println("Opcao invalida!");
                    break;
            }
        } while(opcao != 'R');
    }

    private void listar() {
        try {
            System.out.println("\nMinhas perguntas");
            System.out.println("----------------");

            mapPerguntas.clear();

            // Lê todos os pares disponíveis na Árvore B+
            ArrayList<ParUsuarioPergunta> lista = 
                arqPerguntas.getIndiceUsuarioPergunta().read(null);

            if(lista == null || lista.isEmpty()) {
                System.out.println("\nVocê ainda não possui perguntas.");
                return;
            }

            int numero = 1;

            for(ParUsuarioPergunta par : lista) {
                // Filtra para mostrar apenas as do utilizador logado
                if(par.getIdUsuario() == usuario.getId()) {
                    Pergunta p = arqPerguntas.read(par.getIdPergunta());

                    if(p != null) {
                        // O estado só é mostrado quando a pergunta está
                        // arquivada; o ID real nunca aparece na tela.
                        System.out.println("\n(" + numero + ")" +
                            (p.ativa ? "" : " ARQUIVADA"));
                        System.out.println(Formato.dataHora(p.criacao));
                        System.out.println(p.pergunta);
                        System.out.println("Palavras-chave: " + p.palavrasChave);

                        // Guarda o ID real na posição da lista
                        mapPerguntas.add(p.getId());
                        numero++;
                    }
                }
            }

            if(numero == 1) {
                System.out.println("\nVocê ainda não possui perguntas.");
            }

        } catch(Exception e) {
            System.out.println("\nErro ao listar perguntas!");
            e.printStackTrace();
        }
    }

    private void incluir() {
        try {
            System.out.println("\nIncluir pergunta");
            System.out.println("----------------");
            System.out.print("Pergunta (vazio para cancelar): ");
            String textoPergunta = console.nextLine();

            if(textoPergunta.isEmpty()) {
                return;
            }

            System.out.print("Palavras-chave (separadas por ;): ");
            String palavrasChave = console.nextLine();

            if(palavrasChave.isEmpty()) {
                System.out.println("\nPalavras-chave inválidas.");
                return;
            }

            Pergunta p = new Pergunta(
                usuario.getId(),
                textoPergunta,
                palavrasChave
            );

            arqPerguntas.create(p);
            System.out.println("\nPergunta cadastrada com sucesso!");

        } catch(Exception e) {
            System.out.println("\nErro ao cadastrar pergunta!");
            e.printStackTrace();
        }
    }

    private void alterar() {
        try {
            System.out.println("\nAlterar pergunta");
            System.out.println("----------------");

            if (mapPerguntas.isEmpty()) {
                System.out.println("Nenhuma pergunta carregada. Por favor, liste as perguntas primeiro (Opção A).");
                return;
            }

            System.out.print("Digite o número da pergunta que deseja alterar (0 para cancelar): ");
            int numero = Integer.parseInt(console.nextLine());

            if (numero == 0) return;

            if (numero < 1 || numero > mapPerguntas.size()) {
                System.out.println("Número inválido.");
                return;
            }

            // O mapeamento funciona assim: a pergunta 1 está no índice 0 da lista
            int idReal = mapPerguntas.get(numero - 1);
            
            // Busca o registro completo no arquivo usando o ID real
            Pergunta p = arqPerguntas.read(idReal);

            if (p != null) {
                System.out.println("\nPergunta atual: " + p.pergunta);
                System.out.print("Nova pergunta (deixe vazio para não alterar): ");
                String novaPergunta = console.nextLine();
                
                if (!novaPergunta.isEmpty()) {
                    p.pergunta = novaPergunta;
                }

                System.out.println("Palavras-chave atuais: " + p.palavrasChave);
                System.out.print("Novas palavras-chave (deixe vazio para não alterar): ");
                String novasPalavras = console.nextLine();
                
                if (!novasPalavras.isEmpty()) {
                    p.palavrasChave = novasPalavras;
                }

                // Atualiza a data de alteração sem mexer nos outros campos
                p.alteracao = System.currentTimeMillis();

                if (arqPerguntas.update(p)) {
                    System.out.println("\nPergunta alterada com sucesso!");
                } else {
                    System.out.println("\nNão foi possível atualizar a pergunta.");
                }
            } else {
                System.out.println("\nErro: Pergunta não encontrada no arquivo.");
            }

        } catch (NumberFormatException e) {
            System.out.println("\nNúmero inválido! Digite apenas números.");
        } catch (Exception e) {
            System.out.println("\nErro ao alterar a pergunta!");
            e.printStackTrace();
        }
    }

        private void arquivar() {
        try {
            System.out.println("\nArquivar pergunta");
            System.out.println("-----------------");

            if (mapPerguntas.isEmpty()) {
                System.out.println("Nenhuma pergunta carregada. Por favor, liste as perguntas primeiro (Opção A).");
                return;
            }

            System.out.print("Digite o número da pergunta que deseja arquivar (0 para cancelar): ");
            int numero = Integer.parseInt(console.nextLine());

            if (numero == 0) return;

            if (numero < 1 || numero > mapPerguntas.size()) {
                System.out.println("Número inválido.");
                return;
            }

            // Pega o ID real usando a mesma lógica do Alterar
            int idReal = mapPerguntas.get(numero - 1);
            Pergunta p = arqPerguntas.read(idReal);

            if (p != null) {
                if (!p.ativa) {
                    System.out.println("\nEsta pergunta já está arquivada.");
                    return;
                }

                System.out.println("\nPergunta: " + p.pergunta);
                System.out.print("Confirma o arquivamento desta pergunta? (S/N): ");
                
                String resposta = console.nextLine().toUpperCase();
                char confirma = resposta.isEmpty() ? ' ' : resposta.charAt(0);

                if (confirma == 'S') {
                    p.ativa = false; // Desativa a pergunta
                    p.alteracao = System.currentTimeMillis();

                    if (arqPerguntas.update(p)) {
                        System.out.println("\nPergunta arquivada com sucesso!");
                    } else {
                        System.out.println("\nNão foi possível arquivar a pergunta.");
                    }
                } else {
                    System.out.println("\nArquivamento cancelado.");
                }
            } else {
                System.out.println("\nErro: Pergunta não encontrada no arquivo.");
            }

        } catch (NumberFormatException e) {
            System.out.println("\nNúmero inválido! Digite apenas números.");
        } catch (Exception e) {
            System.out.println("\nErro ao arquivar a pergunta!");
            e.printStackTrace();
        }
    }
    }
