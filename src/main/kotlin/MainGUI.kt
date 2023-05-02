
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JToggleButton

class MainGUI(title: String) : JFrame(title) {

    private var isStarted = false
    private var selectedFile = "---"

    init {
        setSize(600, 400)
        setLocationRelativeTo(null)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true

        // start / stop button
        val startStopBtn = JToggleButton("Start / Stop")
        startStopBtn.addActionListener {
            isStarted = startStopBtn.isSelected
            updateStatus()
        }
        add(startStopBtn)

        // configuration button
        val configBtn = JButton("Configuration")
        add(configBtn)

        // status label
        val statusLabel = JLabel("Status: ")
        val status = JLabel("Inactive")
        add(statusLabel)
        add(status)

        // file label
        val fileLabel = JLabel("Current DB: ")
        val fileSelected = JLabel(selectedFile)
        add(fileLabel)
        add(fileSelected)
    }

    // update status label based on start/stop button status
    private fun updateStatus() {
        val statusLabel = contentPane.getComponent(2) as JLabel
        val status = contentPane.getComponent(3) as JLabel
        if (isStarted) {
            status.text = "Active"
        } else {
            status.text = "Inactive"
        }
    }

    // update selected file label with the file name
    private fun updateSelectedFile(fileName: String) {
        selectedFile = fileName
        val fileSelected = contentPane.getComponent(5) as JLabel
        fileSelected.text = fileName
    }

}
