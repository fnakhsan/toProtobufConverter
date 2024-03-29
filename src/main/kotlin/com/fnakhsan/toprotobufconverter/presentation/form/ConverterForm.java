package com.fnakhsan.toprotobufconverter.presentation.form;

import com.intellij.ui.components.JBScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import javax.swing.*;

public class ConverterForm {
    public RSyntaxTextArea kotlinTextArea;
    public JTextField fileName;
    public JButton btnConvert;
    private JLabel labelFileName;
    public JRadioButton rbKotlin;
    private JLabel labelLanguageSource;
    private JPanel panelSource;
    private JPanel panelControl;
    private JPanel panelLanguage;
    private JPanel panelGeneral;
    private JLabel labelProtobuf;
    private JPanel panelNumeric;
    private JLabel labelNumeric;
    public JRadioButton rbDefaultNum;
    public JRadioButton rbUnsignedNum;
    public JRadioButton rbSignedNum;
    public JRadioButton rbFixedNum;
    public JRadioButton rbSignedFixedNum;
    private JScrollPane panelKotlin;
    public JPanel rootPanel;


    private void createUIComponents() {
        kotlinTextArea = new RSyntaxTextArea();
        kotlinTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_KOTLIN);
        kotlinTextArea.isCodeFoldingEnabled();
        applyTheme(kotlinTextArea);
        panelKotlin = new JBScrollPane(kotlinTextArea);
    }

    private void applyTheme(RSyntaxTextArea textArea) {
        try {
            final String THEME = "/org/fife/ui/rsyntaxtextarea/themes/idea.xml";
            Theme.load(getClass().getResourceAsStream(THEME)).apply(textArea);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Error Dialog", JOptionPane.ERROR_MESSAGE);
        }
    }
}
