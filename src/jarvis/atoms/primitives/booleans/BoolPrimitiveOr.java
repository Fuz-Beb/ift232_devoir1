package jarvis.atoms.primitives.booleans;

import java.util.ArrayList;

import jarvis.atoms.AbstractAtom;
import jarvis.atoms.BoolAtom;
import jarvis.atoms.ObjectAtom;
import jarvis.atoms.primitives.PrimitiveOperationAtom;
import jarvis.interpreter.JarvisInterpreter;

public class BoolPrimitiveOr extends PrimitiveOperationAtom
{
    @Override
    public String makeKey() {
        return "IntegerPrimitiveOr";
    }
    
    @Override
    protected AbstractAtom execute(JarvisInterpreter ji, ObjectAtom self) {

        // Premier argument qui contient un attribut "value"
        BoolAtom val1 = (BoolAtom) self.message("value");
        
        // Deuxième argument placé dans la file
        AbstractAtom arg = ji.getArg();
        BoolAtom val2;
        
        
        // Ce deuxième argument peut être un un BoolAtom ou un boolean
        if (arg instanceof BoolAtom) {
            val2 = (BoolAtom) arg;
        } else {
            val2 = (BoolAtom) ((ObjectAtom) arg).message("value");
        }

        ArrayList<AbstractAtom> data = new ArrayList<AbstractAtom>();
        
        // Operation || 
        data.add(new BoolAtom(val1.getValue() || val2.getValue()));

        return new ObjectAtom(((ObjectAtom) ji.getEnvironment().get("bool")), data, ji);
    }

    @Override
    protected void init() {
        argCount = 1;
    }
}