package ru.sfedu.mmcs.portfolio.swing.chart;

import java.util.ArrayList;
import java.util.List;

public interface EntityClickListner {
	public class EntityClickSource {
		private List<EntityClickListner> _listners = new ArrayList<EntityClickListner>();
		public synchronized void addEventListner(EntityClickListner l){
			_listners.add(l);
		}

		public synchronized void removeEventListner(EntityClickListner l){
			_listners.remove(l);
		}
		
		protected synchronized void fireEntityClick(EventEntityClick e) {
			for(EntityClickListner l : _listners)
				l.entityClicked(e);
		}
	}
	
	public abstract void entityClicked(EventEntityClick e);
}
