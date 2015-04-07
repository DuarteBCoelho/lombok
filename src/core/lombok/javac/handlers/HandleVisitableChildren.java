/*
 * Copyright (C) 2010-2015 The Project Lombok Authors.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lombok.javac.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.VisitableChildren;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.ResolutionResetNeeded;

import org.mangosdk.spi.ProviderFor;

import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCTypeApply;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;

@ProviderFor(JavacAnnotationHandler.class)
@ResolutionResetNeeded
@HandlerPriority(5)
public class HandleVisitableChildren extends JavacAnnotationHandler<VisitableChildren> {
	
	private static Map<String, List<JCVariableDecl>> map = new HashMap<String, List<JCVariableDecl>>();
	
	@Override public void handle(AnnotationValues<VisitableChildren> annotation, JCAnnotation ast, JavacNode annotationNode) {
		JavacNode typeNode = annotationNode.up();
		JCVariableDecl field = (JCVariableDecl) typeNode.get();
		
		JCClassDecl parentClass = (JCClassDecl) typeNode.up().get();
		
		JCTypeApply type = (JCTypeApply) field.getType();
//		Types types = Types.instance(typeNode.getContext());
		
		
		if(!type.type.toString().equals("java.util.List"))
			annotation.setError(null, "only on List");
				
		String className = parentClass.sym.type.toString();
		
		List<JCVariableDecl> list = map.get(className);
		if(list == null) {
			list = new ArrayList<JCVariableDecl>();
			map.put(className, list);
		}
		list.add(field);
	}
	
	static List<JCVariableDecl> getChildrenVariables(String className) {
		return map.containsKey(className) ? map.get(className) : Collections.<JCVariableDecl>emptyList();
	}
	
	static boolean hasChildren(String className) {
		return map.containsKey(className);
	}
	
}
