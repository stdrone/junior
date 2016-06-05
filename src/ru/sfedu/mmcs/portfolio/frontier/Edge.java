package ru.sfedu.mmcs.portfolio.frontier;

import ru.sfedu.mmcs.portfolio.Portfolio;

public abstract class Edge {
	public Edge() {}
	
	public abstract Portfolio calcPortfolio(double value);
	public abstract int size();
	public abstract double getM(int item);
	public abstract double calcV(double m);
	public abstract boolean contains(double value);

	public double getV(int item) {
		return calcV(getM(item));
	};
	
	public abstract String getName();
}
