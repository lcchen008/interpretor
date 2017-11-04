package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import backend.Interpreter;

import errorhandling.LispException;
import frontend.Parser;
import frontend.Sexp;

public class Ui extends JFrame implements ActionListener{

	private static final long serialVersionUID = (long) 9F;
	private JPanel iopanel = new JPanel();
	
	private JScrollPane outputPane = new JScrollPane();
	private JScrollPane inputPane = new JScrollPane();
	private JPanel buttonPane = new JPanel();
	public JButton jButton = new JButton("submit");
	public JTextArea outputTextArea = null;
	public JTextArea inputTextArea = null;
	public Parser parser;
	public Interpreter interpreter;
	
	public void initialize()
	{
		GridLayout grid = new GridLayout(2, 1, 0, 20);
		iopanel.setLayout(grid);
		this.setLayout(new BorderLayout());
		this.setBounds(0, 0, 270, 370);
	
		outputPane.setViewportView(getJTextArea());
		outputPane.setSize(260, 150);
	
		inputPane.setViewportView(getJTextArea1());
		inputPane.setSize(260, 150);
		
		this.getContentPane().add(buttonPane);
		buttonPane.setLayout(new BorderLayout());
		buttonPane.setSize(260, 30);
		jButton.setFont(new Font("Courier", Font.PLAIN, 36));
		buttonPane.add(jButton, BorderLayout.EAST);
	
		jButton.setText("submit");
		outputTextArea.setEditable(false);
		
		this.getContentPane().add(iopanel, BorderLayout.CENTER);
		this.getContentPane().add(buttonPane, BorderLayout.SOUTH);
		iopanel.add(outputPane);
		iopanel.add(inputPane);
		this.jButton.addActionListener(this);
		
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	private JTextArea getJTextArea() {
		if (outputTextArea == null) {
			outputTextArea = new JTextArea();
		}
		return outputTextArea;
	}
	
	private JTextArea getJTextArea1() {
		if (inputTextArea == null) {
			inputTextArea = new JTextArea();
		}
		return inputTextArea;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String s = "";
		parser = new Parser(inputTextArea.getText());
		interpreter = new Interpreter();
		
		Sexp exp;
		
		try
		{
			exp = parser.input();
			exp = interpreter.eval(exp, new Sexp());
			s+=Parser.output2(exp);
			s+="\n";
		}catch(LispException e1)
		{
			System.out.println(e1.errormsg());
		}
		
		while(parser.skipToken())
		{
			try
			{
				exp = parser.input();
				exp = interpreter.eval(exp, new Sexp());
				s+=Parser.output2(exp);
				s+="\n";
			}
			catch(LispException e1){System.out.println(e1.errormsg());}
		}
		outputTextArea.setText(s);
	}
}
