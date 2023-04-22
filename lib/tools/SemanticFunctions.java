//*****************************************************************
// Tratamiento de errores sintácticos
//
// Fichero:    SemanticFunctions.java
// Fecha:      03/03/2022
// Versión:    v1.0
// Asignatura: Procesadores de Lenguajes, curso 2021-2022
//*****************************************************************

package lib.tools;

import java.util.*;
import traductor.Token;
import lib.attributes.*;
import lib.symbolTable.*;
import lib.symbolTable.exceptions.*;
import lib.errores.*;
import lib.tools.codeGeneration.*;

public class SemanticFunctions {

	private ErrorSemantico errSem; //clase común de errores semánticos

	public SemanticFunctions() {
		errSem = new ErrorSemantico();
	}

	public void variable_array(Token t1, Token t2, Attributes at, SymbolTable st) {
		Symbol s = null;
        if (at.parClass == Symbol.ParameterClass.VAL || at.parClass == Symbol.ParameterClass.REF) {
            s = new SymbolArray(t1.image, 0, Integer.parseInt(t2.image)-1,
                                at.type, at.parClass);
            at.parList.add(s);
        }
        else {
            s = new SymbolArray(t1.image, 0, Integer.parseInt(t2.image)-1,
                                at.type);
        }
        try {
            CGUtils.memorySpaces[st.level]++;
            s.dir = CGUtils.memorySpaces[st.level] + 2;
            st.insertSymbol(s);
        }
        catch (AlreadyDefinedSymbolException e){
            errSem.deteccion(e, t1);
        }
	}

	public void variable(Token t1, Attributes at, SymbolTable st) {
		Symbol s = null;
        if (at.parClass == Symbol.ParameterClass.VAL || at.parClass == Symbol.ParameterClass.REF) {
            if (at.type == Symbol.Types.INT) {
                s = new SymbolInt(t1.image, at.parClass);
            }
            else if (at.type == Symbol.Types.CHAR) {
                s = new SymbolChar(t1.image, at.parClass);
            }
            else {
                s = new SymbolBool(t1.image, at.parClass);
            }
            at.parList.add(s);
        }
        else {  
            if (at.type == Symbol.Types.INT) {
                s = new SymbolInt(t1.image);
            }
            else if (at.type == Symbol.Types.CHAR) {
                s = new SymbolChar(t1.image);
            }
            else {
                s = new SymbolBool(t1.image);
            }
        }
        try {
            CGUtils.memorySpaces[st.level]++;
            s.dir = CGUtils.memorySpaces[st.level] + 2;
            st.insertSymbol(s);
        }
        catch (AlreadyDefinedSymbolException e) {
            errSem.deteccion(e, t1);
        }
	}

	public Symbol declaracion_func_proc(Token t, Attributes at, Attributes at1, Attributes at2, SymbolTable st) {
		Symbol s = null;
		if (!t.image.contentEquals("main")) {
            at.parList = new ArrayList<Symbol>();
            if (at1.type == Symbol.Types.PROCEDURE) {
                s = new SymbolProcedure(t.image, at.parList);
            }
            else {
                s = new SymbolFunction(t.image, at.parList, at1.type);
            }
            try {
                s.label = CGUtils.newLabel();
                at.code.addLabel(s.label);
                st.insertSymbol(s);
                at.nivel = s.nivel;
            }
            catch (AlreadyDefinedSymbolException e) {
                errSem.deteccion(e, t);
            }
            st.insertBlock();
            CGUtils.memorySpaces[st.level] = 0;
            at2.parList = at.parList;
            at2.type = at1.type;
        }
        at2.name = t.image;
		return s;
	}

	public void parametro(Attributes at, Attributes at1, Attributes at2, Attributes at3) {
		at3.type = at1.type;
        if (at2.parClass != Symbol.ParameterClass.REF) {
            at3.parClass = Symbol.ParameterClass.VAL;
        }
        else {
            at3.parClass = at2.parClass;
        }
        at3.parList = at.parList;  
	}

    public void inst_asignacion(Token t, Attributes at, Attributes at1, Attributes at2, SymbolTable st) {
        Symbol s = null;
        try {
            s = st.getSymbol(at1.name);
            if (at1.type != Symbol.Types.UNDEFINED && at2.type != Symbol.Types.UNDEFINED) {
                if (at1.type != Symbol.Types.INT && at1.type != Symbol.Types.BOOL && at1.type != Symbol.Types.CHAR && !at1.isCompVector) { 
                    errSem.deteccion("No se puede asignar a " + at1.type.name(), t);
                }
                if (at1.type != at2.type) {
                    errSem.deteccion("Tipos incompatibles en asignación: " + at1.type.name() + "/" + at2.type.name(), t);
                }
            }
        }
        catch (SymbolNotFoundException e) {
            errSem.deteccion(e, t);
        }
    }

	public void expresion(Attributes at, Attributes at1, Attributes at2) {
		at.type = at1.type;
        at.name = at1.name;
        at.beginLine = at1.beginLine;
        at.beginColumn = at1.beginColumn;
        at2.type = at1.type;
        at.arraySize = at1.arraySize;
        at.isCompVector = at1.isCompVector;
        at.isVar = at1.isVar;
        at.isConst = at1.isConst;
        at.baseType = at1.baseType;
	}

	public void relacion1(Attributes at, Attributes at1, Attributes at2) {
		at.type = at1.type;
        at.name = at1.name;
        at.beginLine = at1.beginLine;
        at.beginColumn = at1.beginColumn;
        at2.type = at1.type;
        at.arraySize = at1.arraySize;
        at.isCompVector = at1.isCompVector;
        at.isVar = at1.isVar;
        at.isConst = at1.isConst;
        at.baseType = at1.baseType;
	}

	public void relacion2(Attributes at, Attributes at1, Attributes at2, Attributes at3) {
		if (at1.type == at3.type) {
			at.type = Symbol.Types.BOOL;
		}
		else {
			at.type = Symbol.Types.UNDEFINED;
			errSem.deteccion("Tipos incompatibles en expresión: " + at1.type.name() + "/" + at3.type.name(), at3);
		}
		at.name = at3.name;
		at.beginColumn = at3.beginColumn;
		at.beginLine = at3.beginLine;
		at.isCompVector = at3.isCompVector;
		at.isVar = at3.isVar;
		at.isConst = at3.isConst;
		at.baseType = at3.baseType;
	}

	public void expresion_simple1(Attributes at, Attributes at1, Attributes at2) {
        if (at1.type == Symbol.Types.INT && at2.type != Symbol.Types.INT) {
            errSem.deteccion("Tipos incompatibles en expresión: INT/" + at2.type.name(), at2);
        }
        at.type = at2.type; 
        at.beginLine = at2.beginLine;
        at.beginColumn = at2.beginColumn;
        at.name = at2.name;
        at.arraySize = at2.arraySize;
        at.isCompVector = at2.isCompVector;
        at.isVar = at2.isVar;
        at.isConst = at2.isConst;
        at.baseType = at2.baseType;
	}

	public void expresion_simple2(Attributes at, Attributes at2, Attributes at4) {
		if ((at2.type == at4.type && at2.type == Symbol.Types.INT) || at2.type == Symbol.Types.UNDEFINED || at4.type == Symbol.Types.UNDEFINED) {
			at.type = at4.type;
			at.name = at4.name;
			at.beginLine = at4.beginLine;
			at.beginColumn = at4.beginColumn;
			at.arraySize = at4.arraySize;
			at.isCompVector = at4.isCompVector;
			at.isVar = at4.isVar;
			at.isConst = at4.isConst;
			at.baseType = at4.baseType;
		}
		else {
			if (at2.type != Symbol.Types.INT) {
				errSem.deteccion("Tipos incompatibles en expresión: " + at2.type.name() + "/INT/" + at4.type.name(), at2);
			}
			else {
				errSem.deteccion("Tipos incompatibles en expresión: " + at2.type.name() + "/INT/" + at4.type.name(), at4);
			}
            at.type = Symbol.Types.INT;
		}
	}

    public void termino1(Attributes at, Attributes at1) {
        at.type = at1.type; 
        at.name = at1.name;
        at.beginLine = at1.beginLine;
        at.beginColumn = at1.beginColumn;
        at.arraySize = at1.arraySize;
        at.isCompVector = at1.isCompVector;
        at.isVar = at1.isVar;
        at.isConst = at1.isConst;
        at.baseType = at1.baseType;
    }

    public void termino2(Attributes at, Attributes at1, Attributes at3) {
        if ((at1.type == at3.type && at1.type == Symbol.Types.INT) || at1.type == Symbol.Types.UNDEFINED || at3.type == Symbol.Types.UNDEFINED) {
            at.type = at3.type;
            at.name = at3.name;
            at.beginLine = at3.beginLine;
            at.beginColumn = at3.beginColumn;
            at.arraySize = at3.arraySize;
            at.isCompVector = at3.isCompVector;
            at.isVar = at3.isVar;
            at.isConst = at3.isConst;
            at.baseType = at3.baseType;
        }
        else {
            if (at1.type != Symbol.Types.INT) {
                errSem.deteccion("Tipos incompatibles en expresión: " + at1.type.name() + "/INT/" + at3.type.name(), at1);
            }
            else {
                errSem.deteccion("Tipos incompatibles en expresión: " + at1.type.name() + "/INT/" + at3.type.name(), at3);
            }
            at.type = Symbol.Types.INT;
        }
    }

    public void factor1(Attributes at, Attributes at1) {
        at.type = at1.type;
        at.name = at1.name;
        at.beginLine = at1.beginLine;
        at.beginColumn = at1.beginColumn;
        at.arraySize = at1.arraySize;
        at.isCompVector = at1.isCompVector;
        at.isVar = at1.isVar;
        at.isConst = at1.isConst;
        at.baseType = at1.baseType;
    }

    public void factor2(Attributes at, Attributes at1) {
        if (at1.type != Symbol.Types.BOOL) {
            errSem.deteccion("'not' requiere una expresión 'boolean'", at1);
            at.type = Symbol.Types.UNDEFINED;
        }
        else {
            at.type = at1.type;
        }
        at.name = at1.name;
        at.beginLine = at1.beginLine;
        at.beginColumn = at1.beginColumn;
        at.arraySize = at1.arraySize;
        at.isCompVector = at1.isCompVector;
        at.isVar = at1.isVar;
        at.isConst = at1.isConst;
        at.baseType = at1.baseType;
    }

    public void primario(Attributes at, Attributes at1){
        at.type = at1.type;
        at.name = at1.name;
        at.beginLine = at1.beginLine;
        at.beginColumn = at1.beginColumn;
        at.arraySize = at1.arraySize;
        at.isCompVector = at1.isCompVector;
        at.isVar = at1.isVar;
        at.isConst = at1.isConst;
        at.baseType = at1.baseType;
    }

    public void int2char(Attributes at, Attributes at1) {
        if (at1.type != Symbol.Types.INT) {
            errSem.deteccion("'int2char' requiere una expresión 'int'", at1);
            at.type = Symbol.Types.UNDEFINED;
        }
        else {
            at.type = Symbol.Types.CHAR;
        }
        at.name = at1.name;
        at.beginLine = at1.beginLine;
        at.beginColumn = at1.beginColumn;
        at.arraySize = at1.arraySize;
        at.isCompVector = at1.isCompVector;
        at.isVar = at1.isVar;
        at.isConst = at1.isConst;
        at.baseType = at1.baseType;
    }

    public void char2int(Attributes at, Attributes at1) {
        if (at1.type != Symbol.Types.CHAR) {
            errSem.deteccion("'char2int' requiere una expresión 'character'", at1);
            at.type = Symbol.Types.UNDEFINED;
        }
        else {
            at.type = Symbol.Types.INT;
        }
        at.name = at1.name;
        at.beginLine = at1.beginLine;
        at.beginColumn = at1.beginColumn;
        at.arraySize = at1.arraySize;
        at.isCompVector = at1.isCompVector;
        at.isVar = at1.isVar;
        at.isConst = at1.isConst;
        at.baseType = at1.baseType;
    }

    public void invoc_func(Token t, Attributes at, SymbolTable st) {
        Symbol s = null;
        try {
            s = st.getSymbol(t.image);
            if (!(s instanceof SymbolFunction)) {
                errSem.deteccion("Tiene que ser una invocación a función", t);
            }
            else {
                at.isVar = false;
                at.type = ((SymbolFunction) s).returnType;
                ArrayList<Symbol> parametros = ((SymbolFunction) s).parList;
                if (parametros.size() != at.lExps.size()) {
                    errSem.deteccion("Diferente número de parámetros reales y formales", t);
                }
                else {
                    for (int i = 0; i < at.lExps.size() && i < parametros.size(); i++) {
                        if (at.lExps.get(i).type != parametros.get(i).type) {
                            errSem.deteccion("Incompatibilidad de tipos entre parámetro formal y real", t);
                        }
                        else {
                            if (parametros.get(i).parClass == Symbol.ParameterClass.REF) {
                                if (!at.lExps.get(i).isVar && !at.lExps.get(i).isCompVector) {
                                    errSem.deteccion("El parámetro real para un paso por referencia tiene que ser un asignable", t);
                                }
                                at.lExps.get(i).code.removeLastInst();
                            }
                            if (parametros.get(i).type == Symbol.Types.ARRAY) {
                                if (((SymbolArray) parametros.get(i)).baseType != at.lExps.get(i).baseType) {
                                    errSem.deteccion("Incompatibilidad de tipos entre parámetro formal y real", t);
                                }
                                if (((SymbolArray) parametros.get(i)).maxInd+1 != at.lExps.get(i).arraySize) {
                                    errSem.deteccion("El parámetro real tiene que ser un vector compatible con el formal", t);
                                }
                            }
                            at.code.addBlock(at.lExps.get(i).code);
                        }
                    }
                }
                at.code.addOSFInst(CGUtils.memorySpaces[st.level] + 3, st.level - s.nivel, s.label);
            }
        }
        catch (SymbolNotFoundException e) {
            errSem.deteccion(e, t);
        }
    }

    public void comp_vector(Token t, Attributes at, Attributes at1, SymbolTable st) {
        Symbol s = null;
        try {
            s = st.getSymbol(t.image);
            if (at1.type != Symbol.Types.INT) {
                errSem.deteccion("El selector de un array debe ser de tipo entero", t);
            }
            at.name = s.name;  
        }
        catch (SymbolNotFoundException e) {
            errSem.deteccion(e, t);
        }
        at.isCompVector = true;
        at.isVar = true;
        at.isConst = false;
        at.type = ((SymbolArray) s).baseType;
        at.beginLine = t.beginLine;
        at.beginColumn = t.beginColumn;
    }

    public void identificador(Token t, Attributes at, SymbolTable st) {
        Symbol s = null;
        try {
            s = st.getSymbol(t.image);
        }
        catch (SymbolNotFoundException e) {
            errSem.deteccion(e, t);
        }
        if (s.type == Symbol.Types.ARRAY) {
            at.arraySize = ((SymbolArray) s).maxInd + 1;
            at.baseType = ((SymbolArray) s).baseType;
        }
        at.type = s.type;
        at.isVar = true;
        at.isCompVector = false;
        at.isConst = false;
        at.beginLine = t.beginLine;
        at.beginColumn = t.beginColumn;
        at.name = t.image;
        at.code.addInst(PCodeInstruction.OpCode.SRF, st.level - s.nivel, s.dir);
        at.code.addInst(PCodeInstruction.OpCode.DRF);
    }

    public void const_int(Attributes at, Token t) {
        at.type = Symbol.Types.INT;
        at.isVar = false;
        at.isCompVector = false;
        at.isConst = true;
        at.valInt = Integer.parseInt(t.image);
        at.beginLine = t.beginLine;
        at.beginColumn = t.beginColumn;
        at.name = t.image;
    }

    public void const_char(Attributes at, Token t) {
        at.type = Symbol.Types.CHAR;
        at.isVar = false;
        at.isCompVector = false;
        at.isConst = true;
        at.valChar = t.image.charAt(0);
        at.beginLine = t.beginLine;
        at.beginColumn = t.beginColumn;
        at.name = t.image;
    }

    public void string(Attributes at, Token t) {
        at.type = Symbol.Types.STRING;
        at.isVar = false;
        at.isCompVector = false;
        at.isConst = true;
        at.valString = t.image;
        at.beginLine = t.beginLine;
        at.beginColumn = t.beginColumn;
        at.name = t.image;
    }

    public void const_bool_true(Token t, Attributes at) {
        at.type = Symbol.Types.BOOL;
        at.isVar = false;
        at.isCompVector = false;
        at.isConst = true;
        at.valBool = true;
        at.beginLine = t.beginLine;
        at.beginColumn = t.beginColumn;
        at.name = t.image;
    }

    public void const_bool_false(Token t, Attributes at) {
        at.type = Symbol.Types.BOOL;
        at.isVar = false;
        at.isCompVector = false;
        at.isConst = true;
        at.valBool = false;
        at.beginLine = t.beginLine;
        at.beginColumn = t.beginColumn;
        at.name = t.image;
    }

    public void asignable_comp_vector(Token t, Attributes at, Attributes at1, SymbolTable st) {
        Symbol s = null;
        try {
            s = st.getSymbol(t.image);
            if (at1.type != Symbol.Types.INT) {
                errSem.deteccion("El selector de un array debe ser de tipo INT", t);
            }
        }
        catch (SymbolNotFoundException e) {
            errSem.deteccion(e, t);
        }
        at.isVar = true;
        at.isCompVector = true; 
        at.isConst = false;
        at.name = t.image;
        at.type = ((SymbolArray) s).baseType;
        at.beginLine = t.beginLine;
        at.beginColumn = t.beginColumn;
        at.nivel = st.level - s.nivel;
        at.code.addBlock(at1.code);
        at.code.addInst(PCodeInstruction.OpCode.SRF, st.level - s.nivel, s.dir);
        at.code.addInst(PCodeInstruction.OpCode.PLUS);
    }

    public void asignable(Token t, Attributes at, SymbolTable st) {
        Symbol s = null;
        try {
            s = st.getSymbol(t.image);
            at.name = s.name;
            at.type = s.type;
        }
        catch (SymbolNotFoundException e) {
            errSem.deteccion(e, t);
        }
        at.isVar = true;
        at.isCompVector = false; 
        at.isConst = false;
        at.name = t.image;
        at.type = s.type;
        at.beginLine = t.beginLine;
        at.beginColumn = t.beginColumn;
        at.code.addInst(PCodeInstruction.OpCode.SRF, st.level - s.nivel, s.dir);
    }

    public void inst_invoc_proc(Token t, Attributes at, SymbolTable st) {
        Symbol s = null;
        try {
            s = st.getSymbol(t.image);
            if (s.name.contentEquals("main")) {
                errSem.deteccion("No se puede invocar a 'main'", t);
            }
        }
        catch (SymbolNotFoundException e) {
            errSem.deteccion(e, t);
        }
        ArrayList<Symbol> parametros = ((SymbolProcedure) s).parList;
        if (parametros.size() != at.lExps.size()) {
            errSem.deteccion("Diferente número de parámetros reales y formales", t);
        }
        else {
            for (int i = 0; i < at.lExps.size() && i < parametros.size(); i++) {
                if (at.lExps.get(i).type != parametros.get(i).type) {
                    errSem.deteccion("Incompatibilidad de tipos entre parámetro formal y real", t);
                }
                else {
                    if (parametros.get(i).parClass == Symbol.ParameterClass.REF) {
                        if (!at.lExps.get(i).isVar && !at.lExps.get(i).isCompVector) {
                            errSem.deteccion("El parámetro real para un paso por referencia tiene que ser un asignable", t);
                        }
                        at.lExps.get(i).code.removeLastInst();
                    }
                    if (parametros.get(i).type == Symbol.Types.ARRAY) {
                        if (((SymbolArray) parametros.get(i)).baseType != at.lExps.get(i).baseType) {
                            errSem.deteccion("Incompatibilidad de tipos entre parámetro formal y real", t);
                        }
                        if (((SymbolArray) parametros.get(i)).maxInd+1 != at.lExps.get(i).arraySize) {
                            errSem.deteccion("El parámetro real tiene que ser un vector compatible con el formal", t);
                        }
                    }
                    at.code.addBlock(at.lExps.get(i).code);
                }
            }
            at.code.addOSFInst(CGUtils.memorySpaces[st.level] + 3, st.level - s.nivel, s.label);
        }
    }

    public void lista_cero_o_mas_asignables(Attributes at, Attributes at1) {
        at.type = at1.type;
        at.name = at1.name;
        at.beginColumn = at1.beginColumn;
        at.beginLine = at1.beginLine;
    }

    public void lista_uno_o_mas_asignables(Attributes at, Attributes at2) {
        if (at2.type != Symbol.Types.CHAR && at2.type != Symbol.Types.INT) {
            errSem.deteccion("No se admite un argumento " + at2.type.name() + " en un 'read'", at2);
        }
        else {
            at.type = at2.type;
            at.name = at2.name;
            at.beginColumn = at2.beginColumn;
            at.beginLine = at2.beginLine;
        }
    }
    
}
