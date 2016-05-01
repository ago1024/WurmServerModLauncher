package org.gotti.wurmunlimited.modloader.classhooks;

import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;

/**
 * Replace byte code fragments with other byte code.
 */
public class CodeReplacer {

	private CodeAttribute codeAttribute;

	/**
	 * Initialize code replace.
	 * 
	 * @param codeAttribute
	 */
	public CodeReplacer(CodeAttribute codeAttribute) {
		this.codeAttribute = codeAttribute;
	}

	/**
	 * Replace the code
	 * @param search Code to replace
	 * @param replacement Replacement
	 * @throws NotFoundException The code to replace was not found
	 * @throws BadBytecode Something was wrong with the byte code
	 */
	public void replaceCode(byte[] search, byte[] replacement) throws NotFoundException, BadBytecode {
		int pos = findCode(search);
		
		if (replacement.length <= search.length) {
			// Replacement is shorter or the same length as the code to replace
			// Replace the code and insert nops at the end
			writeWithNops(pos, replacement, search.length - replacement.length);
		} else {
			// Replacement is longer than the original code
			// Insert nops first, then write the replacement over nops and code to replace
			writeOverlapping(pos, replacement, replacement.length - search.length);
		}
	}

	private void writeOverlapping(int pos, byte[] replacement, int overlapping) throws BadBytecode {
		CodeIterator codeIterator = codeAttribute.iterator();
		codeIterator.insertGap(pos, overlapping);
		codeIterator.write(replacement, pos);
	}

	private void writeWithNops(int pos, byte[] replacement, int gap) {
		byte[] nops = new byte[gap];
		CodeIterator codeIterator = codeAttribute.iterator();
		codeIterator.write(replacement, pos);
		codeIterator.write(nops, pos + replacement.length); 
	}

	// Find the code fragment
	private int findCode(byte[] search) throws NotFoundException {
		byte[] code = codeAttribute.getCode();
		for (int i = 0, j = 0, backtrack = 0; i < code.length && j < search.length; i++) {
			if (code[i] == search[j]) {
				if (j == 0) {
					backtrack = i;
				}
				j++;
				if (j == search.length) {
					return backtrack;
				}
			} else if (j > 0) {
				i = backtrack;
				j = 0;
			}
		}
		throw new NotFoundException("code");
	}
}
