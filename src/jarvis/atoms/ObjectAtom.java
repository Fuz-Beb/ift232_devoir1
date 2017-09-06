package jarvis.atoms;

import jarvis.interpreter.JarvisInterpreter;

import java.util.ArrayList;

/*
 * Cette classe implante l'objet de base.
 * L'interpr�teur comprend un objet comme
 * une simple liste de valeurs.
 * L'organisation de ses donn�es est sp�cifi�e
 * par la classe. Celle-ci peut �tre retrouv�e
 * via le lien classReference.
 */
public class ObjectAtom extends AbstractAtom {

	/*
	 * Si vous ajoutez des champs � JarvisClass ces constantes doivent le
	 * refl�ter. Elles sont utilis�es pour retrouver les membres d'une classe.
	 * 
	 */
	public static final int ATTRIBUTE_FIELD = 0;
	public static final int METHOD_FIELD = 1;

	/*
	 * Référence à la classe de cet objet.
	 */
	private ObjectAtom classReference;
	private ArrayList<AbstractAtom> values;
	
	/*
	 * Référence à sa super classe
	 */
	private ObjectAtom superclassReference;
	
	// Référence utile pour faire des reverse lookup
	private JarvisInterpreter ji;

	// Constructeur d'objet générique
	// Utilisé comme raccourci par les fonctions tricheuses.
	public ObjectAtom(ObjectAtom theClass, ArrayList<AbstractAtom> vals, JarvisInterpreter ji) {

		classReference = theClass;

		// Garde une référence du dernier ObjectAtom présent dans le tableau "vals" 
		for (int i = 0; i < vals.size(); i++) {
			if (vals.get(i) instanceof ObjectAtom) {
			    superclassReference = (ObjectAtom) vals.get(i);
			}
		}
		
		values = new ArrayList<AbstractAtom>();
		values.addAll(vals);

		this.ji = ji;
	}

	@Override
	public AbstractAtom interpretNoPut(JarvisInterpreter ji) {
		return this;
	}

	public ObjectAtom getJarvisClass() {
		return classReference;
	}

	// Cas sp�cial o� le selecteur n'est pas encore encapsul� dans un atome
	// Support� pour all�ger la syntaxe.
	public AbstractAtom message(String selector) {

		return message(new StringAtom(selector));

	}

	// HéRITAGE
	// VARIABLESCLASSE
	/*
	 * Algorithme de gestion des messages. Ce bout de code a pour responsabilit�
	 * de d�terminer si le message concerne un attribut ou une m�thode. Pour
	 * implanter l'h�ritage, cet algorithme doit n�cessairement �tre modifi�.
	 */
	public AbstractAtom message(AbstractAtom selector) {

		// Va chercher les attributs
		ListAtom members = (ListAtom) classReference.values.get(ATTRIBUTE_FIELD);

		// Vérifie si c'est un attribut
		int pos = members.find(selector);

		if (pos == -1) {
			// pas un attribut...
			DictionnaryAtom methods = (DictionnaryAtom) classReference.values.get(METHOD_FIELD);

			// Cherche dans le dictionnaire de la classe parente
			AbstractAtom res = getMethod(selector.makeKey(), true);
			
			if (res == null) {
				// Rien ne correspond au message
				return new StringAtom("ComprendPas " + selector);
			} else {
				// C'est une méthode.
				return res;
			}
		}

		else {
			// C'est un attribut.
			return values.get(pos);
		}
	}

	public void setClass(ObjectAtom theClass) {
		classReference = theClass;
	}

	// Surtout utile pour l'affichage dans ce cas-ci...
	@Override
	public String makeKey() {
		String s = "";
		int i = 0;

		s += "\"" + ji.getEnvironment().reverseLookup(classReference) + "\":";

		for (AbstractAtom atom : values) {

			s += " " + ((ListAtom) classReference.values.get(0)).get(i).makeKey() + ":";
			if (atom instanceof ClosureAtom) {
				s += atom;
			} else {
				s += atom.makeKey();
			}

			i++;
		}

		return s;
	}

	public String findClassName(JarvisInterpreter ji) {

		return ji.getEnvironment().reverseLookup(classReference);
	}

	// Parcours via récursivité les méthodes de chacune des classes présentes dans la chaine d'héritage
	private AbstractAtom getMethod(String key, boolean childClass) {

		DictionnaryAtom methods = null;		
		ObjectAtom superclass = null;
		AbstractAtom res = null;
		
		// Récupération de la liste des méthodes
		if (childClass) {
			methods = (DictionnaryAtom) classReference.values.get(METHOD_FIELD);
		} else {
			methods = (DictionnaryAtom) values.get(METHOD_FIELD);
		}
		
		res = methods.get(key);
		
		// Si la méthode n'existe pas, parcours des classes supérieur dans la chaine d'héritage
		if (res == null) {
			if (childClass) {
				superclass = classReference.superclassReference;
			} else {
				superclass = superclassReference;
			}
			
			// Récursivité - La classe parente n'a pas été atteinte. Il y a donc d'autre classe à parcourir
			if (superclass != null) {
				res = superclass.getMethod(key, false);
			}
		}
		return res;
	}
}
