import javax.swing.JButton;

public class KalahPitButton extends JButton
{

	private static final long serialVersionUID = 1L;

	private boolean isEndZone;
	private int index;
	private boolean isPlayerPit;
	
	int getIndex()
	{
		return index;
	}
	
	boolean getIsEndZone()
	{
		return isEndZone;
	}
	
	boolean getIsPlayerPit()
	{
		return isPlayerPit;
	}
	
	void setIndex(int index)
	{
		this.index = index;
	}
	
	void setIsEndZone(boolean isEndZone)
	{
		this.isEndZone = isEndZone;
	}
	
	void setIsPlayerPit(boolean isPlayerPit)
	{
		this.isPlayerPit = isPlayerPit;
	}
}
