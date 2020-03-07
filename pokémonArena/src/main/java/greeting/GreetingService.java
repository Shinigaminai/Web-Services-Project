package greeting;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetingService {

    public String greeting(String name) {
        System.out.println(name);
        return "hello " + name;
    }

}
