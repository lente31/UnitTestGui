package UnitTestGui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;


import turban.utils.ReflectionUtils;

public class Testsammlungsklasse extends JFrame {



	// Array um Testklassen zu speichern
	private Class[] classArr = { Testklasse1.class, Testklasse2.class };

	// Alles für den Baum
	private DefaultTreeModel treeModel;
	private JTree jTree;
	private FlexibleTreeNode<MyGuifiableObject> root, testkl1, testkl2;
	private TestRunners tr = new TestRunners();

	// Alles für JFrame

	private JButton start;
	private JButton stop;
	private JButton xml;
	private JPanel panel;
	private JLabel progress = new JLabel("");

	// Instanzen der Testklassen
	private Testklasse1 tk1 = new Testklasse1();
	private Testklasse2 tk2 = new Testklasse2();
	// Ergebnisse der Testklassen mittels getter
	private List<String> listResults = tk1.getList();
	private List<String> listResults2 = tk2.getList();

	// Zeitmessung
	private Timestamp timeBeg;
	private Timestamp timeEnd;

	// Booleans XML und SQL
	private boolean blnXml = false;
	private boolean blnSql = false;

	public Testsammlungsklasse() {

	}

	/*
	 * erstellt nur den Anfangsbaum, später werden die Knoten überschrieben um die
	 * neuen Informationen darstellen zu könnenah
	 */
	private void createTree() {
		root = new FlexibleTreeNode<>(new MyGuifiableObject("Testsammlungen"));
		testkl1 = new FlexibleTreeNode<>(new MyGuifiableObject("Testsammlungsklasse1"));
		testkl2 = new FlexibleTreeNode<>(new MyGuifiableObject("Testsammlungsklasse2"));

		// alles zu root hinzufügen
		root.add(testkl1);
		root.add(testkl2);

		treeModel = new DefaultTreeModel(root);
		jTree = new JTree(treeModel);
		
	
	}

	// Methode um Gui zu erstellen
	private void frame() {
		panel = new JPanel();

		JFrame frame = new JFrame("Abgabe PMT");

		frame.add(new JScrollPane(jTree));

		// test anzeige
		start = new JButton("Start");
		stop = new JButton("Stop");
		xml = new JButton("XML");

		panel.add(start);
		panel.add(stop);
		panel.add(xml);

		frame.add(panel, BorderLayout.SOUTH);
		frame.add(progress, BorderLayout.NORTH);

		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(new Dimension(500,600)); 
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	// Annotationklasse mit ForEach
	private void annotationsFin() {
		for (Class cl : classArr) {
			List<String> listTests = ReflectionUtils.getMethodNamesWithAnnotation(cl, Test.class);
			List<String> listIgnored = ReflectionUtils.getMethodNamesWithAnnotation(cl, Ignore.class);

			if (cl.equals(Testklasse1.class)) {
				Collections.sort(listTests);
				for (String s : listTests) {
					testkl1.add(new FlexibleTreeNode<>(new MyGuifiableObject(s)));
				}
				for (String s : listIgnored) {
					testkl1.add(new FlexibleTreeNode<>(new MyGuifiableObject(s)));
				}
			} else {
				Collections.sort(listTests);
				for (String s : listTests) {
					testkl2.add(new FlexibleTreeNode<>(new MyGuifiableObject(s)));
				}
				for (String s : listIgnored) {
					testkl2.add(new FlexibleTreeNode<>(new MyGuifiableObject(s)));
				}

			}
		}
	}

	

	/*
	 * Fügt die Ergebnisse der Tests an die Knoten im Gui
	 * tk wird übergeben damit er für TK1 nicht zweimal die Ergebnisse schreibt
	 */
	
	private void addTestResultsFin(int tk) {

		List<String> listTests = ReflectionUtils.getMethodNamesWithAnnotation(classArr[tk], Test.class);
		List<String> listIgnored = ReflectionUtils.getMethodNamesWithAnnotation(classArr[tk], Ignore.class);

		if (classArr[tk].equals(Testklasse1.class)) {
			Collections.sort(listTests);
			for (int j = 0; j < listTests.size(); j++) {

				testkl1.add(new FlexibleTreeNode<>(new MyGuifiableObject(listTests.get(j) + " " + listResults.get(j))));
			}
			for (String s : listIgnored) {
				testkl1.add(new FlexibleTreeNode<>(new MyGuifiableObject("ignoriert: " + s)));
			}
		}

		if (classArr[tk].equals(Testklasse2.class)) {
			Collections.sort(listTests);
			for (int k = 0; k < listTests.size(); k++) {
				testkl2.add(
						new FlexibleTreeNode<>(new MyGuifiableObject(listTests.get(k) + " " + listResults2.get(k))));
			}
			for (String s : listIgnored) {
				testkl2.add(new FlexibleTreeNode<>(new MyGuifiableObject("ignoriert: " + s)));
			}
		}

	}

	// int i gibt Index für Testklasse im Arr an
	// int cl ist für die Notation der Testklasse, also 1 oder 2
	private void startButton(int i, int cl) throws InterruptedException {

		Result result = JUnitCore.runClasses(classArr[i]);
		while (tr.STOP_TEST == false) {
			Thread.sleep(100);
			if (tr.STOP_TEST) {
				break;
			} else if (result.wasSuccessful() == false) {
				if (classArr[i].equals(Testklasse1.class)) {
					root.remove(testkl1);
					testkl1 = new FlexibleTreeNode<>(
							new MyGuifiableObject("Testklasse" + cl + ": Mindestens 1 Test ist fehlgeschlagen"));
					root.insert(testkl1, i);
					try {
						addTestResultsFin(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					root.remove(testkl2);
					testkl2 = new FlexibleTreeNode<>(
							new MyGuifiableObject("Testklasse" + cl + ": Mindestens 1 Test ist fehlgeschlagen"));
					root.insert(testkl2, i);
					try {
						addTestResultsFin(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} else {
				if (classArr[i].equals(Testklasse1.class)) {
					root.remove(testkl1);
					testkl1 = new FlexibleTreeNode<>(
							new MyGuifiableObject("Testklasse" + cl + ": Alle Tests sind durchgelaufen!"));
					root.insert(testkl1, i);
					try {

						addTestResultsFin(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					root.remove(testkl2);
					testkl2 = new FlexibleTreeNode<>(
							new MyGuifiableObject("Testklasse" + cl + ": Alle Tests sind durchgelaufen!"));
					root.insert(testkl2, i);
					try {

						addTestResultsFin(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
			if (i == 1) {
				progress.setText("Alle Tests abgeschlossen!");

			}

			System.out.println("Ergebnisse von TK" + cl + ": ");
			if (i == 0) {
				System.out.println(listResults);
			} else {
				System.out.println(listResults2);
			}

			// nach Test durchlauf können die bools für XML und SQL auf true gesetzt werden
			blnXml = true;
			blnSql = true;

			break;
		}

	}

	public void functions() {
		// alle Methoden aufrufen um später alles mit einer Methode zu starten
		createTree();
		frame();
		annotationsFin();

		// Start-Button, soll Tests starten
		start.addActionListener(ea -> {
			TestRunners.STOP_TEST = false;

			// mit SwingWorker den Prozess im Hintergrund laufen lassen, Gui bleibt
			// benutzbar
			new SwingWorker() {
				@Override
				protected Object doInBackground() throws Exception {

					Date date = new Date();
					// Anfangszeitpunkt merken
					Timestamp time1 = new Timestamp(date.getTime());
					System.out.println("Start-Zeit: " + time1);

					// ausgelagerte Methode ruft für beide Testklassen den Testablauf auf
					startButton(0, 1);
					startButton(1, 2);

					// Endzeit merken
					Date date2 = new Date();
					Timestamp time2 = new Timestamp(date2.getTime());
					System.out.println("Endzeit: " + time2);
					timeBeg = time1;
					timeEnd = time2;

					// Baum neuladen nach beiden durchläufen
					treeModel.reload();

					return null;

				}
			}.execute();

		});
		// SwingWorker entkoppelt die Aufgaben und lässt die Gui nutzbar
		stop.addActionListener(ea -> new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {
				// Testrunner stoppen und damit Tests abbrechen
				tr.STOP_TEST = true;
				System.out.println("Tests wurden abgebrochen");
				progress.setText("Tests wurden abgebrochen!");
				// Listen wieder "reinigen"
				listResults.clear();
				listResults2.clear();
				return null;
			}

		}.execute());

		// Gibt möglichkeit einzelne Knoten auszuwählen, Progress Bar passt sich an.
		// Wichtig für SQL Eintrag
		jTree.addTreeSelectionListener(e -> {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
			if (node == null) {
				// Nichts ausgewählt
				progress.setText("Test auswählen für Status Meldung");
				return;
			}

			// Wenn der String des Nodes Passed, failed oder Ignoriert ist wird der Progress
			// Text angepasst
			else if (node.toString().contains("PASSED")) {
				progress.setText("Test erfolgreich durchlaufen");

			} else if (node.toString().contains("FAILED")) {
				progress.setText("Test ist fehlgeschlagen");

			} else if (node.toString().contains("ignoriert")) {
				progress.setText("Ignorierte Methode");

			}
			// Wäre hier für SQL Eintrag, diese Funktion habe ich jedoch aus Zeigründen
			// weggelassen
			else if (node.toString().contains("SQL-Eintrag erstellen") && blnSql == true) {
//				try {
//					createSQL(); die Methode würde dann hier aufgerufen werden
//				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
			} else {
				progress.setText(" ");
			}

		});

		// Funktion hinter XML Button
		xml.addActionListener(e -> {
			// erst möglich wenn die Tests einmal durchlaufen wurden
			if (blnXml) {
				String xml1 = "";
				String xml2 = "";
				String ignored1 = "";
				String ignored2 = "";

				for (Class cl : classArr) {
					List<String> listTests = ReflectionUtils.getMethodNamesWithAnnotation(cl, Test.class);
					List<String> listIgnored = ReflectionUtils.getMethodNamesWithAnnotation(cl, Ignore.class);

					if (cl.equals(Testklasse1.class)) {
						for (int i = 0; i < listTests.size(); i++) {
							Collections.sort(listTests);
							String xmlTest1 = "\t<Test Ergebnis : " + listResults.get(i) + " >\n" + "\t\t"
									+ listTests.get(i) + "\n" + "\t</Test>\n";

							xml1 = xml1 + xmlTest1;

							for (String s : listIgnored) {
								ignored1 = "\t<Ignoriert>\n" + "\t\t" + s + "\n" + "\t</Ignoriert>\n";
							}

						}
					} else if (cl.equals(Testklasse2.class)) {
						Collections.sort(listTests);
						for (int i = 0; i < listTests.size(); i++) {
							Collections.sort(listTests);
							String xmlTest2 = "\t<Test Ergebnis : " + listResults2.get(i) + " >\n" + "\t\t"
									+ listTests.get(i) + "\n" + "\t</Test>\n";
							xml2 = xml2 + xmlTest2;

							for (String s : listIgnored) {
								ignored2 = "\t<Ignoriert>\n" + "\t\t" + s + "\n" + "\t</Ignoriert>\n";
							}

						}
					}
				}
				JFileChooser chooser = new JFileChooser("Zielverzeichnis auswählen");
				chooser.setCurrentDirectory(new File("user/dir"));
				int returnVal = chooser.showSaveDialog(null);

				// FileChooser um Speicherort auszuwählen
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try (FileWriter fw = new FileWriter(chooser.getSelectedFile() + ".xml")) {
						String xmlString1Beg = "<?xml version=\"1.0\" encoding= \"UTF-8\" ?>\n" + "\t<Ergebnisse>\n"
								+ "\n" + "\t<Testklasse1>\n";
						String xmlString1End = "\t</Testklasse1>\n" + "\n";
						String xmlString2Beg = "\t<Testklasse2>\n";
						String xmlString2End = "\t</Testklasse2>\n" + "\n" + "\t</Ergebnisse>\n";
						// per FileWriter dann alle Strings in eine Datei schreiben
						fw.write(xmlString1Beg + xml1 + ignored1 + xmlString1End + xmlString2Beg + xml2 + ignored2
								+ xmlString2End);
						fw.close();

					} catch (IOException e1) {
						e1.printStackTrace();

					}
				}

			}
		});

	}

}
