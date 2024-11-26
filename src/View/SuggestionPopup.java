package View;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.BadLocationException;

public class SuggestionPopup extends JPopupMenu {
    private JTextArea textArea;
    private String lastText = "";
    private int lastCaretPosition = 0;
    
    public SuggestionPopup(JTextArea textArea) {
        this.textArea = textArea;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Add a document listener to track changes
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                handleTextChange();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                handleTextChange();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                handleTextChange();
            }
        });
    }
    
    private void handleTextChange() {
        String currentText = textArea.getText();
        int currentCaret = textArea.getCaretPosition();
        
        // Only update if text or caret position has changed
        if (!currentText.equals(lastText) || currentCaret != lastCaretPosition) {
            lastText = currentText;
            lastCaretPosition = currentCaret;
            setVisible(false);
        }
    }
    
    public void setSuggestions(java.util.List<String> suggestions) {
        removeAll();
        
        try {
            String currentLineText = getCurrentLineText();
            
            for (String suggestion : suggestions) {
                JMenuItem item = new JMenuItem(suggestion);
                item.addActionListener(e -> {
                    insertSuggestion(suggestion);
                    setVisible(false);
                });
                add(item);
            }
            
            if (getComponentCount() > 0) {
                revalidate();
                repaint();
            } else {
                setVisible(false);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            setVisible(false);
        }
    }
    
    private String getCurrentLineText() throws BadLocationException {
        int caretPos = textArea.getCaretPosition();
        int lineNumber = textArea.getLineOfOffset(caretPos);
        int startOffset = textArea.getLineStartOffset(lineNumber);
        int endOffset = textArea.getLineEndOffset(lineNumber);
        return textArea.getText(startOffset, endOffset - startOffset).trim();
    }
    
    private void insertSuggestion(String suggestion) {
        try {
            int caretPos = textArea.getCaretPosition();
            int lineNumber = textArea.getLineOfOffset(caretPos);
            int startOffset = textArea.getLineStartOffset(lineNumber);
            int endOffset = textArea.getLineEndOffset(lineNumber);
            
            // Get text before and after the current line
            String beforeText = textArea.getText(0, startOffset);
            String afterText = textArea.getText(endOffset, textArea.getText().length() - endOffset);
            
            // Combine the text with the new suggestion
            String newText = beforeText + suggestion;
            if (!afterText.startsWith("\n") && !afterText.isEmpty()) {
                newText += "\n";
            }
            newText += afterText;
            
            // Update the text area
            textArea.setText(newText);
            
            // Place caret at the end of the inserted suggestion
            textArea.setCaretPosition(startOffset + suggestion.length());
            
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void show(Component invoker, int x, int y) {
        try {
            int caretPos = textArea.getCaretPosition();
            int lineNumber = textArea.getLineOfOffset(caretPos);
            int startOffset = textArea.getLineStartOffset(lineNumber);
            
            // Get the position of the current line
            Rectangle position = textArea.modelToView(startOffset);
            
            // Show popup below the current line
            super.show(invoker, x, position.y + position.height);
            
        } catch (Exception e) {
            // Fallback to default positioning if there's an error
            super.show(invoker, x, y);
        }
    }
}