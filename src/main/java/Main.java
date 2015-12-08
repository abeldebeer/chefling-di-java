import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.impl.command.CommandContainer;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 08/12/15.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        new Main().init();
    }

    private void init() throws Exception {
        Container container = new CommandContainer();
    }
}
