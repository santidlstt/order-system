import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Clase para generar hashes BCrypt
 * Ejecutar como Java Application
 */
public class HashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "1234";
        String hash = encoder.encode(password);

        System.out.println("==================================");
        System.out.println("Password: " + password);
        System.out.println("Hash BCrypt:");
        System.out.println(hash);
        System.out.println("==================================");

        // Verificar que funciona
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verificación: " + (matches ? "✓ CORRECTO" : "✗ ERROR"));
    }
}
