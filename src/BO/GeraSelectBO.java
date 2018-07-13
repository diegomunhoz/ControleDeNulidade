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
 * 
 *         CLASSE RESPONSAVEL POR GERAR AS CONDICOES(IFs), VARIAVEIS (WRKs), E O
 *         COMANDO DE SELECAO PARA TRATAMENTO DE NULIDADE, PARA UTILIZAR ESTA
 *         CLASSE POPULE O ARQUIVO DE ENTRADA.
 */
public class GeraSelectBO {
	private String nomeDaDclgen;
	private List<String> varForNullos = new ArrayList<String>();

	private Scanner leitor;
	private PrintStream gravador;

	private String campoDaTabela;

	private String novoCampoDaTabela;
	private String varForIf;

	private StringBuffer comandoMontado = new StringBuffer();

	public GeraSelectBO(String nomeDaDclgen) {

		this.nomeDaDclgen = nomeDaDclgen;
		this.contrucao();
	}

	private void contrucao() {

		try {
			this.leitor = new Scanner(new FileReader("entradaSelect.txt"));
			this.gravador = new PrintStream("resultSelect.txt");
			while (this.leitor.hasNext()) {
				this.campoDaTabela = (String) this.leitor.nextLine();
				this.campoDaTabela = this.campoDaTabela.trim();
				this.novoCampoDaTabela = this.campoDaTabela.replace('_', '-');
				this.varForIf = ("WRK-" + this.novoCampoDaTabela + "-NULL");

				this.comandoMontado.append("            IF " + varForIf + "\n");
				this.comandoMontado
						.append("                                        EQUAL -1\n");
				this.comandoMontado
						.append("                MOVE SPACES             TO "
								+ this.novoCampoDaTabela + "\n");
				this.comandoMontado
						.append("                                        OF "
								+ this.nomeDaDclgen + "\n");
				this.comandoMontado.append("            END-IF.\n");

				this.varForNullos
						.add("WRK-" + this.novoCampoDaTabela + "-NULL");
				this.gravador.println(this.comandoMontado);

			}

			gravador.println("Variaveis de nulidade para work");
			gravador.println("");
			gravador.println("01  WRK-NULOS.");
			for (String a : this.varForNullos) {
				gravador.println(" 05 " + a);
				gravador
						.println("                                        PIC S9(004) COMP    VALUE ZEROS.");
			}

			this.gravador.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("CONCLUIDO");
	}
}
