package menus;

import java.util.Scanner;

import arquivos.ArquivoUsuario;
import entidades.Usuario;

public class MenuAcesso {
    private static Scanner console = Console.entrada();
    ArquivoUsuario arqUsuarios;

    public MenuAcesso() throws Exception {
        arqUsuarios = new ArquivoUsuario();
    }

    public void menu() {
        char opcao;

        do {
            System.out.println("\n\nAJUDA AÍ 1.0");
            System.out.println("------------");
            System.out.println("A - Login");
            System.out.println("B - Novo usuário");
            System.out.println("S - Sair");
            System.out.print("\nOpção: ");

            try {
                opcao = console.nextLine().toUpperCase().charAt(0);
            } catch(Exception e) {
                opcao = ' ';
            }

            switch(opcao) {
                case 'A':
                    login();
                    break;
                case 'B':
                    novoUsuario();
                    break;
                case 'S':
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while(opcao != 'S');
    }

    private void login() {
        boolean tentarNovamente;

        do {
            tentarNovamente = false;

            System.out.println("\nLogin");
            System.out.println("-----");

            try {
                System.out.print("E-mail (vazio para cancelar): ");
                String email = console.nextLine();

                if(email.isEmpty())
                    return;

                System.out.print("Senha: ");
                String senha = console.nextLine();

                Usuario usuario = arqUsuarios.read(email);

                // O e-mail e a senha são validados de uma só vez, e a mensagem
                // de erro é a mesma nos dois casos: assim não se revela se o
                // que está errado é o e-mail ou a senha.
                if(usuario != null &&
                   Usuario.gerarHash(senha).equals(usuario.hashSenha)) {

                    System.out.println("\nLogin realizado com sucesso!");
                    System.out.println("Bem-vindo(a), " + usuario.nome + "!");

                    try {
                        MenuUsuario menuUsuario = new MenuUsuario(usuario, arqUsuarios);
                        menuUsuario.menu();

                    } catch(Exception e) {
                        System.out.println("Erro ao acessar o menu do usuário!");
                        e.printStackTrace();
                    }

                    return;
                }

                System.out.println("\nNome/e-mail ou senha incorretos.");
                tentarNovamente = falhaNoLogin();

            } catch(Exception e) {
                System.out.println("Erro do sistema. Não foi possível realizar o login!");
                e.printStackTrace();
            }

        } while(tentarNovamente);
    }

    // Opções oferecidas depois de um login que falhou. Retorna true quando o
    // usuário quer digitar o e-mail e a senha de novo.
    private boolean falhaNoLogin() {
        char opcao;

        do {
            System.out.println("\nA - Tentar novamente");
            System.out.println("B - Recuperar senha");
            System.out.println("R - Retornar");
            System.out.print("\nOpção: ");

            try {
                opcao = console.nextLine().toUpperCase().charAt(0);
            } catch(Exception e) {
                opcao = ' ';
            }

            switch(opcao) {
                case 'A':
                    return true;
                case 'B':
                    // Se a senha foi redefinida, volta direto para o login.
                    return recuperarSenha();
                case 'R':
                    return false;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while(true);
    }

    private boolean recuperarSenha() {
        System.out.println("\nRecuperar senha");
        System.out.println("---------------");

        try {
            System.out.print("E-mail (vazio para cancelar): ");
            String email = console.nextLine();

            if(email.isEmpty())
                return false;

            Usuario usuario = arqUsuarios.read(email);

            if(usuario == null) {
                System.out.println("\nNão há nenhum usuário cadastrado com este e-mail.");
                return false;
            }

            System.out.println("\nPergunta secreta: " + usuario.perguntaSecreta);
            System.out.print("Resposta (vazio para cancelar): ");
            String resposta = console.nextLine();

            if(resposta.isEmpty())
                return false;

            // A comparação é feita sobre o hash da resposta normalizada, então
            // acentos e letras maiúsculas não impedem a recuperação.
            if(!Usuario.gerarHashResposta(resposta).equals(usuario.hashRespostaSecreta)) {
                System.out.println("\nResposta incorreta. Não foi possível recuperar a senha.");
                return false;
            }

            System.out.print("\nNova senha (vazio para cancelar): ");
            String novaSenha = console.nextLine();

            if(novaSenha.isEmpty())
                return false;

            System.out.print("Confirme a nova senha: ");
            String confirmacao = console.nextLine();

            if(!novaSenha.equals(confirmacao)) {
                System.out.println("\nAs senhas não conferem.");
                return false;
            }

            usuario.hashSenha = Usuario.gerarHash(novaSenha);

            if(arqUsuarios.update(usuario)) {
                System.out.println("\nSenha alterada com sucesso! Faça o login com a nova senha.");
                return true;
            }

            System.out.println("\nNão foi possível alterar a senha.");
            return false;

        } catch(Exception e) {
            System.out.println("Erro do sistema. Não foi possível recuperar a senha!");
            e.printStackTrace();
            return false;
        }
    }

    private void novoUsuario() {
        System.out.println("\nNovo usuário");

        try {
            System.out.print("E-mail (vazio para cancelar): ");
            String email = console.nextLine();

            if(email.isEmpty())
                return;

            Usuario usuarioExistente = arqUsuarios.read(email);

            if(usuarioExistente != null) {
                System.out.println("Este e-mail já está cadastrado.");
                return;
            }

            System.out.print("Nome completo: ");
            String nome = console.nextLine();

            if(nome.isEmpty()) {
                System.out.println("Nome inválido.");
                return;
            }

            System.out.print("Senha: ");
            String senha = console.nextLine();

            if(senha.isEmpty()) {
                System.out.println("Senha inválida.");
                return;
            }

            System.out.print("Pergunta secreta: ");
            String perguntaSecreta = console.nextLine();

            if(perguntaSecreta.isEmpty()) {
                System.out.println("Pergunta secreta inválida.");
                return;
            }

            System.out.print("Resposta secreta: ");
            String respostaSecreta = console.nextLine();

            if(respostaSecreta.isEmpty()) {
                System.out.println("Resposta secreta inválida.");
                return;
            }

            Usuario u = new Usuario(
                nome,
                email,
                senha,
                perguntaSecreta,
                respostaSecreta
            );

            int id = arqUsuarios.create(u);

            System.out.println("\nUsuário cadastrado com sucesso!");
            System.out.println("ID do usuário: " + id);

        } catch(Exception e) {
            System.out.println(
                "Erro do sistema. Não foi possível cadastrar o usuário!"
            );

            e.printStackTrace();
        }
    }
}