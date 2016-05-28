package ru.sfedu.mmcs.portfolio.methods;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import ru.sfedu.mmcs.portfolio.Portfolio;
import ru.sfedu.mmcs.portfolio.loaders.DataLoader;

public class MethodRaw extends Method{

	public MethodRaw(DataLoader data, Portfolio portfolio) {
		super(data, portfolio);
	}

	@Override
	public Portfolio calculcatePortfolio(Vector2D point) {
		return _portfolio;
	}

	@Override
	public String getName() {
		return " Построение по границе";
	}

}
