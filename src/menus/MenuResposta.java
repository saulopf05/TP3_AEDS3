package menus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import arquivos.ArquivoResposta;
import arquivos.ArquivoUsuario;
import arquivos.ArquivoVoto;
import entidades.Pergunta;
import entidades.Resposta;
import entidades.Usuario;
import entidades.Voto;
import indices.ParPerguntaResposta;
import indices.ParUsuarioVoto;

public class MenuResposta {
    private static Scanner console = Console.entrada();
    private Pergunta pergunta;
    private Usuario usuario;
    private ArquivoResposta arqRespostas;
    private ArquivoUsuario arqUsuarios;
    private ArquivoVoto arqVotos;

    public MenuResposta(Pergunta pergunta, Usuario usuario) throws Exception {
        this.pergunta = pergunta;
        this.usuario = usuario;
        this.arqRespostas = new ArquivoResposta();
        this.arqUsuarios = new ArquivoUsuario();
        this.arqVotos = new ArquivoVoto();
    }

    public void menu() {
        char opcao;

        if (!pergunta.ativa) {
            System.out.println("\nEsta pergunta foi arquivada e não pode receber mais respostas.");
            return;
        }

        do {
            System.out.println("\nPergunta selecionada");
            System.out.println("--------------------");
            System.out.println(pergunta.pergunta);
            System.out.println("\nA - Listar respostas");
            System.out.println("B - Responder");
            System.out.println("C - Votar na resposta");
            System.out.println("R - Retornar");
            System.out.print("\nOpção: ");

            try {
                opcao = console.nextLine().toUpperCase().charAt(0);
            } catch (Exception e) {
                opcao = ' ';
            }

            switch (opcao) {
                case 'A':
                    listar();
                    break;
                case 'B':
                    incluir();
                    break;
                case 'C':
                    votarResposta();
                    break;
                case 'R':
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        } while (opcao != 'R');
    }

    private void listar() {
        try {
            System.out.println("\nRespostas ordenadas pela nota (maior primeiro)");
            System.out.println("---------------------------------------------");
            ArrayList<ParPerguntaResposta> indice = arqRespostas.getIndicePerguntaResposta().read(null);
            ArrayList<Resposta> respostas = new ArrayList<>();
            if (indice != null) {
                for (ParPerguntaResposta par : indice) {
                    if (par.getIdPergunta() == pergunta.getId()) {
                        Resposta r = arqRespostas.read(par.getIdResposta());
                        if (r != null && r.ativa) respostas.add(r);
                    }
                }
            }
            Collections.sort(respostas, new Comparator<Resposta>() {
                @Override public int compare(Resposta a, Resposta b) {
                    int porNota = Short.compare(b.nota, a.nota);
                    if (porNota != 0) return porNota;
                    return Long.compare(b.criacao, a.criacao);
                }
            });
            if (respostas.isEmpty()) {
                System.out.println("\nAinda não há respostas para esta pergunta.");
                return;
            }
            int numero = 1;
            for (Resposta r : respostas) {
                Usuario autor = arqUsuarios.read(r.getIdUsuario());
                System.out.println("\n" + numero + " - " + r.texto);
                System.out.println("Autor: " + (autor != null ? autor.nome : "Desconhecido"));
                System.out.println("Data: " + Formato.dataHora(r.criacao));
                System.out.println("Nota: " + r.nota);
                System.out.println("Quantidade de avaliações: " + contarAvaliacoes(r.getId()));
                numero++;
            }
        } catch (Exception e) {
            System.out.println("\nErro ao listar respostas!");
            e.printStackTrace();
        }
    }

    private int contarAvaliacoes(int idResposta) throws Exception {
        ArrayList<ParUsuarioVoto> pares = arqVotos.getIndiceUsuarioVoto().read(null);
        int total = 0;
        if (pares != null) {
            for (ParUsuarioVoto par : pares) {
                Voto voto = arqVotos.read(par.getIdVoto());
                if (voto != null && voto.getIdResposta() == idResposta) total++;
            }
        }
        return total;
    }

    private void incluir() {
        try {
            if (!pergunta.ativa) {
                System.out.println("\nEsta pergunta foi arquivada e não pode receber respostas.");
                return;
            }

            System.out.println("\nResponder pergunta");
            System.out.println("-----------------");
            System.out.print("Texto da resposta (vazio para cancelar): ");
            String texto = console.nextLine();

            if (texto.isEmpty()) {
                return;
            }

            Resposta resposta = new Resposta(
                    pergunta.getId(),
                    usuario.getId(),
                    texto);

            arqRespostas.create(resposta);
            System.out.println("\nResposta cadastrada com sucesso!");

        } catch (Exception e) {
            System.out.println("\nErro ao responder pergunta!");
            e.printStackTrace();
        }
    }

    private void votarResposta() {
        try {
            ArrayList<ParPerguntaResposta> lista = arqRespostas.getIndicePerguntaResposta().read(null);

            if (lista == null || lista.isEmpty()) {
                System.out.println("\nAinda não há respostas para esta pergunta.");
                return;
            }

            ArrayList<Integer> mapRespostas = new ArrayList<>();
            int numero = 1;

            System.out.println("\nVotar em resposta");
            System.out.println("-----------------");

            for (ParPerguntaResposta par : lista) {
                if (par.getIdPergunta() == pergunta.getId()) {
                    Resposta r = arqRespostas.read(par.getIdResposta());

                    if (r != null && r.ativa) {
                        Usuario autor = arqUsuarios.read(r.getIdUsuario());
                        System.out.println("\n" + numero + " - " + r.texto);
                        System.out.println("Autor: " + (autor != null ? autor.nome : "Desconhecido"));
                        System.out.println("Nota atual: " + r.nota);
                        mapRespostas.add(r.getId());
                        numero++;
                    }
                }
            }

            if (mapRespostas.isEmpty()) {
                System.out.println("\nAinda não há respostas ativas para esta pergunta.");
                return;
            }

            System.out.print("\nDigite o número da resposta para votar (0 para cancelar): ");
            int escolha = Integer.parseInt(console.nextLine());

            if (escolha == 0) {
                return;
            }

            if (escolha < 1 || escolha > mapRespostas.size()) {
                System.out.println("\nNúmero inválido.");
                return;
            }

            Resposta respostaSelecionada = arqRespostas.read(mapRespostas.get(escolha - 1));

            if (respostaSelecionada == null) {
                System.out.println("\nResposta não encontrada.");
                return;
            }

            if (!respostaSelecionada.ativa) {
                System.out.println("\nEsta resposta está inativa e não pode receber votos.");
                return;
            }

            if (respostaSelecionada.getIdUsuario() == usuario.getId()) {
                System.out.println("\nVocê não pode votar na própria resposta.");
                return;
            }

            ArrayList<ParUsuarioVoto> votosUsuario = arqVotos.getIndiceUsuarioVoto().read(null);
            if (votosUsuario != null) {
                for (ParUsuarioVoto par : votosUsuario) {
                    Voto v = arqVotos.read(par.getIdVoto());
                    if (v != null && v.getIdUsuario() == usuario.getId()
                            && v.getIdResposta() == respostaSelecionada.getId()) {
                        System.out.println("\nVocê já votou nesta resposta.");
                        return;
                    }
                }
            }

            System.out.print("Valor do voto (+1 ou -1): ");
            short valor = Short.parseShort(console.nextLine());

            if (valor != 1 && valor != -1) {
                System.out.println("\nValor inválido. Use apenas +1 ou -1.");
                return;
            }

            Voto voto = new Voto(usuario.getId(), pergunta.getId(), respostaSelecionada.getId(), valor);
            arqVotos.create(voto);

            respostaSelecionada.nota = (short) (respostaSelecionada.nota + valor);
            arqRespostas.update(respostaSelecionada);

            System.out.println("\nVoto registrado com sucesso!");

        } catch (NumberFormatException e) {
            System.out.println("\nNúmero inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("\nErro ao votar na resposta!");
            e.printStackTrace();
        }
    }
}
