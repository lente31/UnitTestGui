package UnitTestGui;


import javax.swing.tree.*;
import java.util.*;
import java.util.function.Predicate;

import turban.utils.*;



/**
 * Class for a more flexible TreeNode than DefaultMutableTreeNode.
 * The class is programmed as a generic working on IGuifiable objects
 *
 * @param <T> Typevariable for the type of the user object.
 */
public class FlexibleTreeNode<T extends IGuifiable>  extends DefaultMutableTreeNode implements IDebugable
{
	/**
	 * Constructor
	 * IN: The user object to be retrieved by 'getUserObject()'.
	 */
	public FlexibleTreeNode(T userObj)
	{
		super(userObj);
		// NOTE (BeTu; 2015-05-19): 'super'-Statement must always be first statement in constructor.
		// Therefore the check for != null is performed afterwards. This is not so nice, but it's quite ok. 
		ErrorHandler.Assert(userObj!=null, true, FlexibleTreeNode.class, "No user object provided!");
	}


	@Override
	public void setUserObject(Object userObj)
	{
		throw new UnsupportedOperationException ();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getUserObject()
	{
		return (T) super.getUserObject();
	}

	/**
	 * Performs indentation (Einrückung) according to the level in the tree.
	 */
	private void toDebugString_indent(StringBuilder sb,int indentLvl)
	{
		for (int i=0; i<indentLvl; i++)
		{
			sb.append("  ");
		}
		if(indentLvl>0)
		{
			sb.append("|-");
		}
	}

	@SuppressWarnings("unchecked")
	private void toDebugString_recursive(StringBuilder sb,int indentLvl,FlexibleTreeNode<T> tn )
	{
		toDebugString_indent( sb, indentLvl);
		sb.append(tn.toString());
		sb.append("\n");
		for (FlexibleTreeNode<T> tnChild:tn.getChildren())
		{
			toDebugString_recursive(sb,indentLvl+1,(FlexibleTreeNode<T>) tnChild );
		}
	}

	/**
	 * String to be resolved for Debugging-Purposes
	 * @return Returns the content of the tree for debugging purposes.
	 */
	public String toDebugString()
	{
		try
		{
			StringBuilder sb=new StringBuilder();
			toDebugString_recursive(sb,0,this );
			return sb.toString();
		}
		catch(Throwable th)
		{
			return "Unable to fully resolve " +this.getClass().getName()+" ["+this.toString()+"]";
		}
	}


	/**
	 * Overwritten toString(). The method now calls toGuiString of the generic object to do easier working
	 * with the IGuifiable-Pattern. As this method is used by JTree.
	 *
	 * @return the string to be displayed in the GUI (e.g., in JTree)
	 */
	@Override
	public String toString()
	{
		try
		{
			return ((IGuifiable)this.getUserObject()).toGuiString();
		}
		catch(Throwable th)
		{
			ErrorHandler.logException (th, false,FlexibleTreeNode.class
					, "Error resolving toGuiString()" );
			return this.getClass().getName()+": [Unresolvable Symbol!]";
		}
	}

	/**
	 * Returns the children as Iterator
	 * 
	 * NOTE: Hier als anonyme Implementierung realisiert -> wir nutzen children(), das eine Enumeration zurückgibt.
	 */
	public Iterable<FlexibleTreeNode<T>> getChildren() {
		@SuppressWarnings("rawtypes")
		final Enumeration enumChildren= this.children();
		return new Iterable<FlexibleTreeNode<T>>() { 
			public Iterator<FlexibleTreeNode<T>> iterator() 
			{
				return new Iterator <FlexibleTreeNode<T>> () 
				{
					public boolean hasNext()
					{
						return enumChildren.hasMoreElements();
					}

					@SuppressWarnings("unchecked")
					public FlexibleTreeNode<T> next()
					{
						return (FlexibleTreeNode<T>)enumChildren.nextElement();
					}

					public void remove()
					{
						throw new UnsupportedOperationException ();
					}
				};
			}
		};
	}

	/**
	 * Counts the tree nodes underneath the start node including the start node
	 * @param tnStart IN: The tree node to start
	 * @return the number of nodes
	 */
	public int countTreeNodes(TreeNode tnStart)
	{
		if(tnStart==null)
		{
			return 0;
		}

		int iCount=1; // 0+1 (==this node)
		for(int i = 0; i< ((TreeNode) tnStart).getChildCount(); i++)
		{
			TreeNode tnChild=(TreeNode) ((TreeNode) tnStart).getChildAt(i);
			iCount+=countTreeNodes(tnChild);
		}
		return iCount;
	}



	/**
	 * Gets all tree nodes of the tree defined by tnStart (including tnStart!) as a list
	 * HINWEIS: Diese Methode könnte man eigentlich auch static machen, weil das eigentliche Knotenobjekt gar nicht verwendet wird.
	 *          Ich habe darauf verzichtet, weil wegen des generischen-Typparameters hier sonst Probleme auftreten, die Sie noch mehr verwirren würden.
	 *          -> Sehen Sie hierzu aber die danach auskommentierte Methode, die zeigt, wie man Sie als static-Methode deklarieren müsste - hauptsächlich müsste man dann einen neuen Typparameter einführen.   
	 *
	 * @param tnStart IN: The tree node to start
	 * @return list with the tree nodes.
	 */
	public /*static*/ List<FlexibleTreeNode<T>> getAllTreeNodesAsList(FlexibleTreeNode<T> tnStart)
	{
		//Hinweis: Hier wird der Startknoten übergeben, deshalb können wir eine static-Methode daraus machen.
		List<FlexibleTreeNode<T>> lstToReturn=new ArrayList<FlexibleTreeNode<T>> ();
		if(tnStart==null){
			return lstToReturn; //Return empty list
		}

		lstToReturn.add(tnStart);
		for(int i=0; i< tnStart.getChildCount(); i++){
			@SuppressWarnings("unchecked")
			FlexibleTreeNode<T> tnChild= (FlexibleTreeNode<T>)tnStart.getChildAt(i);
			lstToReturn.addAll(getAllTreeNodesAsList(tnChild));
		}
		return lstToReturn;
	}

	/* Oder static mit Typvariable:
	 public static <R extends IGuifiable> List<FlexibleTreeNode<R>> getAllTreeNodesAsList(FlexibleTreeNode<R> tnStart)
      {
		  //Hinweis: Hier wird der Startknoten übergeben, deshalb können wir eine static-Methode daraus machen.
		  List<FlexibleTreeNode<R>> lstToReturn=new ArrayList<FlexibleTreeNode<R>> ();
		  if(tnStart==null){
			  return lstToReturn; //Return empty list
		  }

		  lstToReturn.add(tnStart);
		  for(int i=0; i< tnStart.getChildCount(); i++){
			  @SuppressWarnings("unchecked")
			  FlexibleTreeNode<R> tnChild= (FlexibleTreeNode<R>)tnStart.getChildAt(i);
			  lstToReturn.addAll(getAllTreeNodesAsList(tnChild));
		  }
	      return lstToReturn;
	  }
	 */



	/**
	 * Gets all tree nodes of the tree defined by tnStart (excluding tnStart!) as List
	 * HINWEIS: Diese Methode könnte man eigentlich auch static machen, weil das eigentliche Knotenobjekt gar nicht verwendet wird.
	 *          Ich habe darauf verzichtet, weil wegen des generischen-Typparameters hier sonst Probleme auftreten, die Sie noch mehr verwirren würden.
	 *
	 * @param tnStart IN: The tree node to start
	 * @return list with the tree nodes.
	 */	   
	public /*static*/ List<FlexibleTreeNode<T>> getAllSubTreeNodesAsList(FlexibleTreeNode<T> tnStart)
	{
		List<FlexibleTreeNode<T>> lstRet=new ArrayList<>(); 
		//Wenn Startknoten dabei sein soll:
		//lstRet.add(tnStart);

		for(int i=0; i< tnStart.getChildCount(); i++){
			FlexibleTreeNode<T> tnChild= (FlexibleTreeNode<T>)tnStart.getChildAt(i);
			lstRet.add(tnChild); //Hier soll er nicht dabei sein.
			List<FlexibleTreeNode<T>> lstCh= getAllSubTreeNodesAsList(tnChild);
			lstRet.addAll(lstCh);
		}

		return lstRet;
	}

	//Verwendung:
	//  FlexibleTreeNode<...> tn= ...
	//  List<FlexibleTreeNode<...>>lst= FlexibleTreeNode.getAllSubTreeNodesAsList(tn);

	private static<Z extends IGuifiable> void getAllTreeNodesAsListMoreEfficient_recursive(FlexibleTreeNode<Z> tnStart, List<FlexibleTreeNode<Z>> lstToReturn){
		lstToReturn.add(tnStart);
		for(int i=0; i< tnStart.getChildCount(); i++){
			@SuppressWarnings("unchecked") 
			FlexibleTreeNode<Z> tnChild= (FlexibleTreeNode<Z>)tnStart.getChildAt(i);
			//lstToReturn.add(tnChild); Für not including
			getAllTreeNodesAsListMoreEfficient_recursive(tnChild,lstToReturn);
		}
	}

	/**
	 * Gets all tree nodes of the tree defined by tnStart (including tnStart!) as List
	 * NOTE: Same as getAllTreeNodesAsList, but more efficient because we first create a list and then use it in the recursion
	 * HINWEIS: Diese Methode könnte man eigentlich auch static machen, weil das eigentliche Knotenobjekt gar nicht verwendet wird.
	 *          Ich habe darauf verzichtet, weil wegen des generischen-Typparameters hier sonst Probleme auftreten, die Sie noch mehr verwirren würden.

	 * @param tnStart IN: The tree node to start
	 * @return list with the tree nodes.
	 */	
	public static<Z extends IGuifiable> List<FlexibleTreeNode<Z>> getAllTreeNodesAsListMoreEfficient(FlexibleTreeNode<Z> tnStart)
	{
		List<FlexibleTreeNode<Z>> lstToReturn=new ArrayList<FlexibleTreeNode<Z>> ();
		if(tnStart==null){
			return lstToReturn;
		}
		getAllTreeNodesAsListMoreEfficient_recursive( tnStart,lstToReturn);
		return lstToReturn;
	}


	public List<FlexibleTreeNode<T>> getAllTreeNodesAsListMoreEfficient() {
		return getAllTreeNodesAsListMoreEfficient(this);
	}






	// FlexibleTreeNode<...> tn= ...
	// tn.getAllTreeNodesAsListMoreEfficient();


	/*
	  @FunctionalInterface
	  public interface Predicate<T>{
		  boolean test(T t);
		  //... (weitere Default-Methoden)
	  }

	  Verwendung getAllNodesWithCondition:
	  FlexibleTreeNode<MyGuifObj> tnRoot=...;
	  List<FlexibleTreeNode<MyGuifObj>> lstFoundTnds= tnRoot.getAllNodesWithCondition(
	    new Predicate<FlexibleTreeNode<MyGuifObj>>(){
			public boolean test(FlexibleTreeNode<MyGuifObj> tn){
				return tn.isLeaf();
				//return tn.getChildCount()>5;
				//return tn.getUserObject() instanceof KlassenTypX;
				//return tn.getUserObject().toGuiString().contains ("test");
			}
		}
	    );
	 */



	/**
	 * Gets all tree nodes of the tree (including the start tree node) meeting a condition
	 *
	 * @param cond IN: The condition to test as interface
	 *
	 * @return the treenodes meeting the condition
	 */
	public List<FlexibleTreeNode<T>> getAllNodesWithCondition (Predicate<FlexibleTreeNode<T>> cond){
		//ToDo: Probieren Sie das mal selbst noch:
		throw new UnsupportedOperationException();
	}


	public List<T> getAllUserObjects(FlexibleTreeNode<T> tnStart){
		
		List<T> lst = null;
		if(tnStart == null) {
			return  lst;//leere Liste ausgeben oder Fehler
		}
		
		for (int i = 0; i < tnStart.getAllTreeNodesAsList(tnStart).size()-1; i++) {
			
			
			
			lst.add(tnStart.getUserObject());
			
			//aber wie recursive aufrufen ?
		}
		
		
		return lst;
		
	}



}