import java.io.InputStream;
import Models.Products;
import Models.Shop;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;

public class ProductsTest {

    @Test
    void jsonVprokContainsProducts() throws Exception {
        InputStream jsonStream = getClass().getClassLoader()
                .getResourceAsStream("products.json");

        ObjectMapper objectMapper = new ObjectMapper();
        Shop shop = objectMapper.readValue(jsonStream, Shop.class);

        Assertions.assertEquals("Vprok.ru", shop.getShop());
        List<Products> products = shop.getProducts();

        Products milk = products.get(0);
        Assertions.assertEquals("M001", milk.getId());
        Assertions.assertEquals("Молоко Простоквашино 3.2%", milk.getName());
        Assertions.assertEquals(89.90, milk.getPrice(), 0.01);
        Assertions.assertTrue(milk.isInStock());

        Products bread = products.get(1);
        Assertions.assertEquals("B002", bread.getId());
        Assertions.assertEquals("Хлеб Бородинский", bread.getName());
        Assertions.assertEquals(45.50, bread.getPrice(), 0.01);
        Assertions.assertTrue(bread.isInStock());

        Products tomatoes = products.get(2);
        Assertions.assertEquals("V003", tomatoes.getId());
        Assertions.assertEquals("Помидорки", tomatoes.getName());
        Assertions.assertEquals(199.90, tomatoes.getPrice(), 0.01);
        Assertions.assertFalse(tomatoes.isInStock());
    }
}