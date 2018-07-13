package test;

import BO.GeraInsertEUpdateBO;

public class TesteMain {
	public static void main(String[] args) {
		// @SuppressWarnings("unused")
		// GeraSelectBO select = new GeraSelectBO("tre_Ssed");

		/**
		 * passar para o construtor o nome do book, nome da sdlgen, nome da
		 * tabela com o banco e o comando sql
		 */
		@SuppressWarnings("unused")
		GeraInsertEUpdateBO insert = new GeraInsertEUpdateBO("TREIW", "TREIB",
				"NOMETABELA", "INSERT");

	}
}
