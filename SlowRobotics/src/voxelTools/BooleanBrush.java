package voxelTools;


public class BooleanBrush {

	float radius,thickness;
	VoxelGrid e;
	VoxelGrid volume;
	float cellRadiusX,cellRadiusY,cellRadiusZ;
	float stretchX,stretchY,stretchZ;

	public BooleanBrush(VoxelGrid v, VoxelGrid _e, float radius, float _thickness) {
		volume = v;
		e = _e;
		thickness = _thickness;
		setSize(radius);
	}


	public void drawAtGridPos(float cx, float cy, float cz, int density) {
		int minX = max((int) (cx - cellRadiusX), 0);
		int minY = max((int) (cy - cellRadiusY), 0);
		int minZ = max((int) (cz - cellRadiusZ), 0);
		int maxX = min((int) (cx + cellRadiusX), volume.getW());
		int maxY = min((int) (cy + cellRadiusY), volume.getH());
		int maxZ = min((int) (cz + cellRadiusZ), volume.getD());
		for (int z = minZ; z < maxZ; z++) {
			for (int y = minY; y < maxY; y++) {
				for (int x = minX; x < maxX; x++) {
					float dx = x - cx;
					float dy = (y - cy) * stretchY;
					float dz = (z - cz) * stretchZ;
					float d = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
					if (d <= cellRadiusX) {
						float cellVal = (1 - d / cellRadiusX) * density;
						Cell idx = volume.get(x, y, z);
						if(e.get(x,y,z).get()<200)idx.set(density);
					}
				}
			}
		}
	}
	
	public int max(int a, int b){
		int r = (a>b)?a:b;
		return r;
	}
	
	public int min(int a, int b){
		int r = (a<b)?a:b;
		return r;
	}

	public void setSize(float radius) {
		radius = radius;
		cellRadiusX = (int) (radius / volume.s.x);
		cellRadiusY = (int) (radius / volume.s.y);
		cellRadiusZ = (int) (radius / volume.s.z);
		stretchY = (float) cellRadiusX / cellRadiusY;
		stretchZ = (float) cellRadiusX / cellRadiusZ;
		// logger.fine("new brush size: " + radius);
	}

	public void fillAtGridPos(float cx, float cy, float cz) {
		int minX = max((int) (cx - cellRadiusX), 0);
		int minY = max((int) (cy - cellRadiusY), 0);
		int minZ = max((int) (cz - cellRadiusZ), 0);
		int maxX = min((int) (cx + cellRadiusX), volume.getW());
		int maxY = min((int) (cy + cellRadiusY), volume.getH());
		int maxZ = min((int) (cz + cellRadiusZ), volume.getD());
		for (int z = minZ; z < maxZ; z++) {
			for (int y = minY; y < maxY; y++) {
				for (int x = minX; x < maxX; x++) {
					float dx = x - cx;
					float dy = (y - cy) * stretchY;
					float dz = (z - cz) * stretchZ;
					float d = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
					if (d <= cellRadiusX) {
						int idx = volume.getIndexFor(x, y, z);
						e.setVoxelAt(idx, 250);
					}
				}
			}
		}
	}

}
