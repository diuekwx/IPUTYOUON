package spotify.recommender.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

@Service
public class EncryptionService
{
    private final SecretKey secretKey;

    public EncryptionService(SecretKey secretKey){
        this.secretKey = secretKey;
    }

    public String encrypted(String accessToken) throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(accessToken.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);

    }
    public String decrypt(String encryptedToken) throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedToken));
        return new String(decryptedBytes);
    }

    public String encryptSafe(String token){
        try{
            return encrypted(token);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String decryptSafe(String token){
        try{
            return decrypt(token);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }




}
