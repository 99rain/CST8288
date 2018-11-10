package joey.assignment2.enums;

public enum ShapeType {
	LINE(2), TRIANGLE(3), SQUARE(4), RECTANGLE(-4), PENTAGON(5), HEXAGON(6),
 CUSTOM(100), IREGULAR(-1); 
		private int value;
		
		private ShapeType(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
}
