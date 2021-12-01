package frontcontroller;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.javalin.Javalin;
import util.StatusObj;

public class FrontController {
    public FrontController(Javalin javalin) {
        javalin.exception(NumberFormatException.class, (e, context) -> {
            context.status(400);
            context.json(new StatusObj(400, "Certain URI or query parameter values need to be numeric, non-numeric value detected."));
        });
        javalin.exception(UnrecognizedPropertyException.class, (e, context) -> {
            context.status(400);
            context.json(new StatusObj(400, "JSON field error check names and number of fields(keys) and formatting."));
        });
        javalin.exception(JsonParseException.class, (e, context) -> {
            context.status(400);
            context.json(new StatusObj(400, "JSON formatting error check formatting of JSON object in body."));
        });
        new Dispatcher(javalin);
    }
}
