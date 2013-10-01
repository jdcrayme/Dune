package game.dune.ii;

import android.view.GestureDetector;
import android.view.MotionEvent;

// ===========================================================
// Sub Class
// ===========================================================
/**
 * This class basically just passes gestures on to the interface class
 */
class UserInterfaceGestureDetector extends GestureDetector.SimpleOnGestureListener {

	private final UserInterface userInterface;	//The user interface

	/**
	 * This class basically just passes gestures on to the interface class
	 * 
	 * @param userInterface
	 */
	UserInterfaceGestureDetector(UserInterface userInterface) {
		this.userInterface = userInterface;
	}
	
	@Override
     public boolean onSingleTapConfirmed(MotionEvent e) { return this.userInterface.onSingleTap(e.getX(),e.getY()); }

     @Override
     public boolean onDoubleTap(MotionEvent e) { return this.userInterface.onDoubleTap(e.getX(),e.getY()); }

     @Override
     public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { return this.userInterface.onFling(velocityX, velocityY); }

     @Override
     public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return this.userInterface.onScroll(distanceX, distanceY); }

     @Override
     public boolean onDown(MotionEvent e) { return this.userInterface.onDown(); }
     
 }