package ch.epfl.javanco.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


public class VectorRepresentation extends AbstractLayerRepresentation {
	
	private static class SubStructure {
		private NodeContainer n;
		private Vector<LinkContainer> outGo;
		private Vector<LinkContainer> inCo;
		private SubStructure(NodeContainer n) {
			this.n = n;
		}
		
		private void addOutgoingLink(int end, LinkContainer lc) {
			if (outGo == null) {
				outGo = new Vector<>();
			}
			if (outGo.size() == 0) {
				outGo.add(lc);
				return;
			}
			LinkContainer cursor = outGo.get(0);
			int counter = 0;
			while (cursor.getEndNodeIndex() < end) {
				counter++;
				if (outGo.size() > counter) {
					cursor = outGo.get(counter);
				} else {
					break;
				}
			}
			if (cursor.getEndNodeIndex() == end) {
				throw new DuplicateLinkException(lc.getStartNodeIndex(), lc.getEndNodeIndex());
			}
			synchronized (outGo) {
				outGo.setSize(outGo.size() + 1);
				for (int i = outGo.size() - 1 ; i > counter ; i--) {
					outGo.set(i, outGo.get(i-1));
				}
				outGo.set(counter, lc);
			}
		}
	
		private void addIncomingLink(int start, LinkContainer lc) {
			if (inCo == null) {
				inCo = new Vector<>();
			}
			if (inCo.size() == 0) {
				inCo.add(lc);
				return;
			}			
			LinkContainer cursor = inCo.get(0);
			int counter = 0;
			while (cursor.getStartNodeIndex() < start) {
				counter++;
				if (inCo.size() > counter) {
					cursor = inCo.get(counter);
				} else {
					break;
				}
			}
			if (cursor.getStartNodeIndex() == start) {
				throw new DuplicateLinkException(lc.getStartNodeIndex(), lc.getEndNodeIndex());
			}		
			synchronized (inCo) {
				inCo.setSize(inCo.size() + 1);
				for (int i = inCo.size() - 1 ; i > counter ; i--) {
					inCo.set(i, inCo.get(i-1));
				}
				inCo.set(counter, lc);
			}
		}

		@Override
		public String toString() {
			return "'" + n.getIndex() + "'";
		}
	}	
	
//	public final static Class<VectorRepresentation> VECTOR_REPRESENTATION = VectorRepresentation.class;

	private Vector<SubStructure> internalArray = new Vector<>(20, 20);
	private ArrayList<SubStructure> internalList = new ArrayList<>();

	VectorRepresentation(LayerContainer agr) {
		super(agr);

	}

	@Override
	List<NodeContainer> getAllNodeContainers() {
		if (internalList == null) {
			return new ArrayList<>(0);
		}
		List<NodeContainer> c = new ArrayList<>(internalList.size());
		synchronized (internalList) {
			LayerContainer current = getLayerContainer();
			for (SubStructure sb : internalList) {
				if (sb.n.getContainingLayerContainer() == current) {
					c.add(sb.n);
				}
			}
		}
		return c;
	}

	@Override
	List<LinkContainer> getAllLinkContainers() {
		if (internalList == null) {
			return VOID_ARRAY;
		}
		List<LinkContainer> c = new LinkedList<>();
		synchronized (internalList) {
			for (SubStructure sb : internalList) {
				Vector<LinkContainer> vec = sb.outGo;
				if (vec != null) {
					c.addAll(vec);
				}
			}
		}
		return c;
	}

	private static final ArrayList<LinkContainer> VOID_ARRAY = new ArrayList<>(0);

	@Override
	protected List<LinkContainer> getOutgoingLinks(int start) {
		if (internalList == null) {
			return VOID_ARRAY;
		}
		if (internalArray.size() <= start) {
			return VOID_ARRAY;
		}
		SubStructure s = internalArray.get(start);
		if (s == null) {
			return VOID_ARRAY;
		}
		List<LinkContainer> c;
		if (s.outGo != null) {
			c = new ArrayList<>(s.outGo.size());
			synchronized (s.outGo) {
				c.addAll(s.outGo);
			}
		} else {
			c = new ArrayList<>(0);
		}
		return c;
	}
	@Override
	protected List<LinkContainer> getIncomingLinks(int end) {
		if (internalList == null) {
			return new ArrayList<>(0);
		}
		SubStructure s = internalArray.get(end);
		if (s == null) {
			return new ArrayList<>(0);
		}
		List<LinkContainer> c;
		if (s.inCo != null) {
			c = new ArrayList<>(s.inCo.size());
			synchronized (s.inCo) {
				c.addAll(s.inCo);
			}
		} else {
			c = new ArrayList<>(0);
		}
		return c;
	}

	@Override
	Collection<LinkContainer>	getLinkContainers(int start, int end){
		if (internalList == null) {
			return new ArrayList<>(0);
		}
		SubStructure s = internalArray.get(start);
		Collection<LinkContainer> c = new ArrayList<>(1);
		if (s != null && s.outGo != null) {
			synchronized (s.outGo) {
				for (int i = 0 ; i < s.outGo.size(); i++) {
					LinkContainer link = s.outGo.get(i);
					if (link.getEndNodeIndex() == end) {
						c.add(link);
						return c;
					}
				}
			}
		}
		return c;
	}

	/**
	 * VIrtual is used to mention if node is really contained on this layer or on another one
	 */
	@Override
	void addNode(NodeContainer nodeContainer, boolean virtual) {
		addNodeInternal(new SubStructure(nodeContainer));
	/*	if (!virtual) {
			nodeContainer.setContainingLayerContainer(this.getLayerContainer());
		}*/
	}

	private void addNodeInternal(SubStructure s) {
		int index = s.n.getIndex();
		if (index < 0) {
			throw new IllegalStateException("Trying to add a node without index, index must be choosen first");
		}
		/*	if ((internalArray.size() > index) && (internalArray.get(index) != null)) {
			throw new IllegalStateException("Trying to add twice a node using the same index");
		}	*/
		if (internalArray.size() < index+1) {
			internalArray.setSize(index+1);
		}
		synchronized (internalArray) {
			internalArray.set(s.n.getIndex(),s);
		}
		synchronized (internalList) {
			internalList.add(s);
		}
	}

	@Override
	void addLink(LinkContainer linkContainer) {
		int start = linkContainer.getStartNodeIndex();
		int end   = linkContainer.getEndNodeIndex();
		int max = Math.max(start, end);
		if (internalArray.size() < max+1) {
			internalArray.setSize(max+1);
		}
		SubStructure startN = internalArray.get(start);
		SubStructure endN = internalArray.get(end);
		if (startN == null) {
			NodeContainer startC = linkContainer.getStartNodeContainer();
			startN = new SubStructure(startC);
			addNodeInternal(startN);
		}
		if (endN == null) {
			NodeContainer endC = linkContainer.getEndNodeContainer();
			endN = new SubStructure(endC);
			addNodeInternal(endN);
		}
		startN.addOutgoingLink(end, linkContainer);
		endN.addIncomingLink(start, linkContainer);
		linkContainer.setContainingLayerContainer(getLayerContainer());
	}
	@Override
	void removeAllLinks() {
		for (SubStructure sb : internalList) {
			if (sb.inCo != null) {
				synchronized (sb.inCo) {
					sb.inCo.clear();
				}
			}
			if (sb.outGo != null) {
				synchronized (sb.outGo) {
					sb.outGo.clear();
				}
			}
		}
	}

	@Override
	AbstractElementContainer removeElement(AbstractElementContainer cont){
		if (cont instanceof NodeContainer) {
			NodeContainer c = (NodeContainer)cont;
			return removeNode(c);
		} else if (cont instanceof LinkContainer) {
			LinkContainer lc = (LinkContainer)cont;
			return removeLink(lc);
		} else {
			throw new IllegalStateException("Cannot remove something else than node or link");
		}
	}

	@Override
	NodeContainer removeNode(NodeContainer node){
		SubStructure s = null;
		synchronized (internalList) {
			s = internalArray.set(node.getIndex(), null);
			internalList.remove(s);			
		}
		if (s == null) {
			assert 0==1 : "Impossible to remove an unexisting element";
			return null;
		}
		return s.n;
	}
	@Override
	LinkContainer removeLink(LinkContainer link){
		int startN = link.getStartNodeIndex();
		int endN   = link.getEndNodeIndex();
		SubStructure substart = internalArray.get(startN);
		SubStructure subend   = internalArray.get(endN);
		if (substart == null || subend == null) {
			throw new IllegalStateException("Cannot remove link if connecting node does not exist");
		}
		removeInternal(substart.outGo, link);
		removeInternal(subend.inCo, link);
		return link;
	}
	
	private void removeInternal(Vector<LinkContainer> og, LinkContainer link) {
		synchronized (og) {
			int startIndexForShift = -1;
			for (int i = 0 ; i < og.size() ; i++) {
				LinkContainer lc = og.get(i);
				if (lc == link) {
					startIndexForShift = i;
					break;
				}
			}
			if (startIndexForShift >= 0) {
				for (int i = startIndexForShift ; i < og.size()-1 ; i++) {
					og.set(i, og.get(i+1));
				}
				og.setSize(og.size()-1);
			}
		}		
	}
	@Override
	NodeContainer nodeAt(int idx){
		if (internalArray == null) {
			return null;
		}
		synchronized (internalArray) {
			if (internalArray.size() < idx+1) {
				return null;
			}
			if (internalArray.get(idx) == null) {
				return null;
			}
			return internalArray.get(idx).n;
		}
	}

}
