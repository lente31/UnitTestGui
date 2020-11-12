package UnitTestGui;

import java.awt.Image;

import turban.utils.IGuifiable;

public class MyGuifiableObject implements IGuifiable {
	
	public String _strGuiString;
	public MyGuifiableObject(String strGuiString) {
		_strGuiString = strGuiString;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getGuiIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toGuiString() {
		// TODO Auto-generated method stub
		return _strGuiString;
	}

}
