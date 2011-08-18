package source;

import java.util.List;

public class TestRecursion
{
	public Container getSomeContainer(Container parent)
	{
		return new Container();
	}

	public Container2 getSomeContainer2(Container2 parent)
	{
		return new Container2();
	}
	
	public class Container {

		private List<Container> innerContainers;
		private Container parent;
		private Integer id;
		private long serial;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public long getSerial() {
			return serial;
		}

		public void setSerial(long serial) {
			this.serial = serial;
		}

		public void setParent(Container parent) {
			this.parent = parent;
		}

		public Container getParent() {
			return parent;
		}

		public void setInnerContainers(List<Container> innerContainers) {
			this.innerContainers = innerContainers;
		}

		public List<Container> getInnerContainers() {
			return innerContainers;
		}
	}
	
	public class Container2 {

		private Container container;
		private Container container2;
		private List<Container> containers;

		public Container getContainer() {
			return container;
		}

		public void setContainer(Container container) {
			this.container = container;
		}

		public List<Container> getContainers() {
			return containers;
		}

		public void setContainers(List<Container> containers) {
			this.containers = containers;
		}

		public void setContainer2(Container container2) {
			this.container2 = container2;
		}

		public Container getContainer2() {
			return container2;
		}
	}
}
