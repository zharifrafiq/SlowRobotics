package voxelTools;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import toxi.geom.Vec3D;
import toxi.geom.mesh.DefaultSelector;
import toxi.geom.mesh.Vertex;
import toxi.geom.mesh.VertexSelector;
import toxi.geom.mesh.WEMeshFilterStrategy;
import toxi.geom.mesh.WETriangleMesh;
import toxi.geom.mesh.WEVertex;


public class ScaledLaplacianSmooth implements WEMeshFilterStrategy {
	
	  public void filter(VertexSelector selector, int numIterations) {
	        final Collection<Vertex> selection = selector.getSelection();
	        if (!(selector.getMesh() instanceof WETriangleMesh)) {
	            throw new IllegalArgumentException(
	                    "This filter requires a WETriangleMesh");
	        }
	        final WETriangleMesh mesh = (WETriangleMesh) selector.getMesh();
	        final HashMap<Vertex, Vec3D> filtered = new HashMap<Vertex, Vec3D>(
	                selection.size());
	        for (int i = 0; i < numIterations; i++) {
	            filtered.clear();
	            for (Vertex v : selection) {
	                final Vec3D laplacian = new Vec3D();
	                final List<WEVertex> neighbours = ((WEVertex) v).getNeighbors();
	                for (WEVertex n : neighbours) {
	                    laplacian.addSelf(n);
	                }
	                laplacian.scaleSelf(1f / neighbours.size());
	                laplacian.interpolateToSelf(v,(float) 0.5);
	                filtered.put(v, laplacian);
	            }
	            for (Vertex v : filtered.keySet()) {
	                mesh.vertices.get(v).set(filtered.get(v));
	            }
	            mesh.rebuildIndex();
	        }
	        mesh.computeFaceNormals();
	        mesh.computeVertexNormals();
	    }

	    public void filter(WETriangleMesh mesh, int numIterations) {
	        filter(new DefaultSelector(mesh).selectVertices(), numIterations);
	    }
}
