package jarvis.atoms.primitives.booleans;

import java.util.ArrayList;

import jarvis.atoms.AbstractAtom;
import jarvis.atoms.BoolAtom;
import jarvis.atoms.ObjectAtom;
import jarvis.atoms.primitives.PrimitiveOperationAtom;
import jarvis.interpreter.JarvisInterpreter;

public class BoolPrimitiveNot extends PrimitiveOperationAtom
{
    @Override
    public String makeKey() {
        return "IntegerPrimitiveNot";
    }
    
    @Override
    protected AbstractAtom execute(JarvisInterpreter ji, ObjectAtom self) {

        // Premier argument qui contient un attribut "value"
        // Pas de deuxième argument, simplement l'inversion du premier
        BoolAtom val = (BoolAtom) self.message("value");

        ArrayList<AbstractAtom> data = new ArrayList<AbstractAtom>();
        
        // Operation not "!"
        data.add(new BoolAtom(!val.getValue()));

        return new ObjectAtom(((ObjectAtom) ji.getEnvironment().get("bool")), data, ji);
    }

    @Override
    protected void init() {
        argCount = 0;
    }
}