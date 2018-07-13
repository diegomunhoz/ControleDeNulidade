package BO;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @author LuiVilella
 * 
 *         PROGRAMA AUXILIAR PARA DESENVOLVIMENTO COBOL
 */
public class GeraInsertEUpdateBO {

	private Scanner leitor;
	private Scanner leitor2;
	private PrintStream gravador;
	private String nomeDoBook;
	private String nomeDaDclgen;
	private String nomeDaTabela;
	private String nomeDoComandoSQL;
	private List<String> varForNullos = new ArrayList<String>();
	private String campoDaTabela;
	private String novoCampoDaTabela;

	private StringBuffer comandoCondicional;
	private StringBuffer comandoMove;
	private StringBuffer comandoSQL1 = new StringBuffer();
	private StringBuffer comandoSQL2 = new StringBuffer();

	/**
	 * PASSAR PARA O CONSTRUTOR NOME DO BOOK (W) NOME DA DCLGEN (B), O NOME DA
	 * TABELA JUNTO O BANCO (DB2TEST.TABELA_TESTE) E O NOME DO COMANDO SQL
	 * (INSERT OU UPDATE), ESTE PROGRAMA FUNCIONA COM DOIS ARQUIVOS DE ENTRADA,
	 * ENTRADANAONOTNULL E' O ARQUIVO ONDE DEVE SER COLOCADO OS CAMPOS PARA O
	 * TRATAMENTO DE NULIDADE PARA SER CRIADA AS CONDICIONAIS, JA NO ARQUIVO
	 * NOTNULL DEVE COLOCAR OS CAMPOS QUE SERAO SOMENTE MOVIMENTACOES, SENDO
	 * ASSIM O PROGRAMA RETORNARA TODAS CONSICIONASI, TODOS AS MOVIMENTACOES,
	 * TODAS VARIAVEIS DE NULIDADE, E O COMANDO DE SQL COM TODOS OS CAMPOS
	 * 
	 * 
	 * @param nomeDoBook
	 * @param nomeDaDclgen
	 * @param nomeDaTabela
	 * @param nomeDoComandoSQL
	 */
	public GeraInsertEUpdateBO(String nomeDoBook, String nomeDaDclgen,
			String nomeDaTabela, String nomeDoComandoSQL) {

		this.nomeDoBook = nomeDoBook;
		this.nomeDaDclgen = nomeDaDclgen;
		this.nomeDaTabela = nomeDaTabela;
		this.nomeDoComandoSQL = nomeDoComandoSQL;
		try {
			if (this.nomeDoBook.isEmpty() || this.nomeDaDclgen.isEmpty()
					|| this.nomeDaTabela.isEmpty()
					|| this.nomeDoComandoSQL.isEmpty()) {
				System.out.println("OS QUATRO PARAMESTROS SAO OBRIGATORIOS");
				return;
			} else if (!this.nomeDoComandoSQL.equals("INSERT")
					&& !this.nomeDoComandoSQL.equals("UPDATE")) {
				System.out
						.println("VERIFIQUE O NOME DO COMANDO SQL (INSERT OU UPDATE)");
				return;
			}
		} catch (Exception e) {
			System.out.println("OS QUATRO PARAMESTROS SAO OBRIGATORIOS");
		}
		this.processar();
	}

	private void processar() {
		try {
			/*
			 * COLOCAR AQUI OS CAMPOS NAO NOT NULL
			 */
			this.leitor = new Scanner(new FileReader("entradaNaoNotNull.txt"));

			/*
			 * COLOCAR AQUI OS CAMPOS NOT NULL
			 */
			this.leitor2 = new Scanner(new FileReader("entradaNotNull.txt"));
			this.gravador = new PrintStream("resultIntoAndUp.txt");

			this.gravador
					.println("#################################################################");
			this.gravador.println("CONSDICIONAIS DOS CAMPOS NAO NOT NULL");
			this.gravador.println("");

			this.leitor.hasNext();
			this.montaCondicionais();
			this.iniciaSQL();

			while (this.leitor.hasNext()) {
				this.montaCondicionais();
				this.montaSQL();
			}

			this.gravador
					.println("#################################################################");
			this.gravador.println("MOVES DE CAMPOS NOT NULL");
			this.gravador.println("");

			while (this.leitor2.hasNext()) {
				this.montaMOVEs();
				this.montaSQLNotNull();
			}

			this.montaWRK();
			this.juntaSQL();
			this.gravador
					.println("#################################################################");
			this.gravador.println("COMANDO SQL");
			this.gravador.println("");
			this.gravador.println(comandoSQL1);

			this.gravador.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		System.out.println("CONCLUIDO");
	}

	private void montaWRK() {
		this.gravador
				.println("#################################################################");
		this.gravador.println("VARIAVEIS DE NULIDADE");
		this.gravador.println("");
		this.gravador.println("01  WRK-NULOS.");
		for (String a : this.varForNullos) {
			this.gravador.println(" 05 " + a);
			this.gravador
					.println("                                        PIC S9(004) COMP    VALUE ZEROS.");

		}

	}

	private void montaSQL() {
		this.comandoSQL1.append("\n                     , "
				+ this.campoDaTabela);

		this.comandoSQL2.append("\n                     , :" + this.nomeDoBook
				+ "." + this.novoCampoDaTabela);

		this.comandoSQL2.append("\n                            :WRK-"
				+ novoCampoDaTabela + "-NULL");
	}

	private void montaSQLNotNull() {
		this.comandoSQL1.append("\n                     , "
				+ this.campoDaTabela);

		this.comandoSQL2.append("\n                     , :" + this.nomeDoBook
				+ "." + this.novoCampoDaTabela);

	}

	private void iniciaSQL() {
		this.comandoSQL1.append("            EXEC SQL\n");
		if (this.nomeDoComandoSQL.equals("INSERT")) {
			this.comandoSQL1.append("                INSERT INTO "
					+ this.nomeDaTabela + "\n");
		} else {
			this.comandoSQL1.append("                UPDATE INTO "
					+ this.nomeDaTabela + "\n");
		}
		this.comandoSQL1.append("                    (" + "\n");
		this.comandoSQL1.append("                       " + this.campoDaTabela);

		this.comandoSQL2.append("\n                    )\n");
		this.comandoSQL2.append("                VALUES\n");
		this.comandoSQL2.append("                    (\n");
		this.comandoSQL2.append("                       :" + this.nomeDoBook
				+ "." + this.novoCampoDaTabela);
		this.comandoSQL2.append("\n                            :WRK-"
				+ novoCampoDaTabela + "-NULL");

	}

	private void juntaSQL() {
		this.comandoSQL2.append("\n                    )\n");
		this.comandoSQL2.append("            END-EXEC.\n");
		this.comandoSQL1.append(this.comandoSQL2);
	}

	private void montaCondicionais() {
		try {

			String varForIf;
			String varForMove1;
			String varForMove2;

			this.campoDaTabela = (String) leitor.nextLine();
			this.campoDaTabela = campoDaTabela.trim();
			this.novoCampoDaTabela = campoDaTabela.replace('_', '-');

			varForIf = (nomeDoBook + "-" + novoCampoDaTabela + "-N");
			varForMove1 = (nomeDoBook + "-" + novoCampoDaTabela);
			varForMove2 = ("WRK-" + novoCampoDaTabela + "-NULL");

			/*
			 * MONTA O COMANDO CONDICIONAL
			 */
			this.comandoCondicional = new StringBuffer();

			this.comandoCondicional.append("            IF " + varForIf + "\n");
			this.comandoCondicional
					.append("                                        NOT EQUAL '?'\n");
			this.comandoCondicional.append("                MOVE "
					+ varForMove1 + "\n");
			this.comandoCondicional
					.append("                                        TO "
							+ this.novoCampoDaTabela + "\n");
			this.comandoCondicional
					.append("                                        OF "
							+ this.nomeDaDclgen + "\n");
			this.comandoCondicional.append("            ELSE\n");
			this.comandoCondicional
					.append("                MOVE -1                 TO "
							+ varForMove2 + "\n");
			this.comandoCondicional.append("            END-IF.\n");

			this.varForNullos.add("WRK-" + this.novoCampoDaTabela + "-NULL");
			this.gravador.println(this.comandoCondicional);

		} catch (Exception e) {
			System.out.println("PROBLEMAS NO AQUIVO DE ENTRADA");
		}
	}

	private void montaMOVEs() {

		this.campoDaTabela = (String) leitor2.nextLine();
		this.campoDaTabela = campoDaTabela.trim();
		this.novoCampoDaTabela = campoDaTabela.replace('_', '-');

		String varForMove = (nomeDoBook + "-" + novoCampoDaTabela);

		/*
		 * MONTA O COMANDO CONDICIONAL
		 */
		this.comandoMove = new StringBuffer();

		this.comandoMove.append("            MOVE " + varForMove + "\n");
		this.comandoMove.append("                                        TO "
				+ this.novoCampoDaTabela + "\n");
		this.comandoMove.append("                                        OF "
				+ this.nomeDaDclgen + "\n");
		this.gravador.println(this.comandoMove);

	}
}
