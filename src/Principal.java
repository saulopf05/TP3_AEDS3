import menus.MenuAcesso;

public class Principal {
    public static void main(String[] args) {
        try {
            MenuAcesso menuAcesso = new MenuAcesso();
            menuAcesso.menu();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
