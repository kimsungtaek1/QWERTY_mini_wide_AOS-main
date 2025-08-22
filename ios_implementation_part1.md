# iOS QWERTY Mini Wide Keyboard Implementation - Part 1: Project Setup & Core Architecture

## 1. Project Setup

### 1.1 Xcode Project Configuration
```
Project Name: QWERTYMiniWide
Bundle Identifier: com.qwertyminiwide.keyboard
Deployment Target: iOS 14.0
Swift Version: 5.0
```

### 1.2 Info.plist Configuration
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleDevelopmentRegion</key>
    <string>$(DEVELOPMENT_LANGUAGE)</string>
    <key>CFBundleDisplayName</key>
    <string>QWERTY Mini Wide</string>
    <key>CFBundleExecutable</key>
    <string>$(EXECUTABLE_NAME)</string>
    <key>CFBundleIdentifier</key>
    <string>$(PRODUCT_BUNDLE_IDENTIFIER)</string>
    <key>CFBundleInfoDictionaryVersion</key>
    <string>6.0</string>
    <key>CFBundleName</key>
    <string>$(PRODUCT_NAME)</string>
    <key>CFBundlePackageType</key>
    <string>$(PRODUCT_BUNDLE_PACKAGE_TYPE)</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0</string>
    <key>CFBundleVersion</key>
    <string>1</string>
    <key>LSRequiresIPhoneOS</key>
    <true/>
    <key>UIApplicationSupportsIndirectInputEvents</key>
    <true/>
    <key>UILaunchStoryboardName</key>
    <string>LaunchScreen</string>
    <key>UIMainStoryboardFile</key>
    <string>Main</string>
    <key>UIRequiredDeviceCapabilities</key>
    <array>
        <string>armv7</string>
    </array>
    <key>UISupportedInterfaceOrientations</key>
    <array>
        <string>UIInterfaceOrientationPortrait</string>
        <string>UIInterfaceOrientationLandscapeLeft</string>
        <string>UIInterfaceOrientationLandscapeRight</string>
    </array>
    <key>NSExtension</key>
    <dict>
        <key>NSExtensionAttributes</key>
        <dict>
            <key>IsASCIICapable</key>
            <false/>
            <key>PrefersRightToLeft</key>
            <false/>
            <key>PrimaryLanguage</key>
            <string>en-US</string>
            <key>RequestsOpenAccess</key>
            <true/>
        </dict>
        <key>NSExtensionPointIdentifier</key>
        <string>com.apple.keyboard-service</string>
        <key>NSExtensionPrincipalClass</key>
        <string>$(PRODUCT_MODULE_NAME).KeyboardViewController</string>
    </dict>
</dict>
</plist>
```

## 2. Core Data Models

### 2.1 KeyboardKey.swift
```swift
import Foundation
import UIKit

enum KeyType {
    case character(String)
    case space
    case backspace
    case enter
    case shift
    case numbers
    case languageChange
    case settings
    case emoji
    case special(String)
    case numberPad(String)
    case symbolPad(String)
}

struct KeyboardKey {
    let type: KeyType
    let label: String
    let shiftLabel: String?
    let longPressKeys: [String]?
    let width: CGFloat
    let height: CGFloat
    var isPressed: Bool = false
    var isShifted: Bool = false
    var isDarkMode: Bool = false
    
    init(type: KeyType, 
         label: String, 
         shiftLabel: String? = nil, 
         longPressKeys: [String]? = nil,
         width: CGFloat = 1.0,
         height: CGFloat = 1.0) {
        self.type = type
        self.label = label
        self.shiftLabel = shiftLabel
        self.longPressKeys = longPressKeys
        self.width = width
        self.height = height
    }
    
    var displayLabel: String {
        if isShifted, let shiftLabel = shiftLabel {
            return shiftLabel
        }
        return label
    }
}

// Haptic Feedback Manager
class HapticManager {
    static let shared = HapticManager()
    private let impactFeedback = UIImpactFeedbackGenerator(style: .light)
    private let selectionFeedback = UISelectionFeedbackGenerator()
    
    private init() {
        impactFeedback.prepare()
        selectionFeedback.prepare()
    }
    
    func triggerKeyPress() {
        impactFeedback.impactOccurred()
    }
    
    func triggerSelection() {
        selectionFeedback.selectionChanged()
    }
}

// Sound Manager
class SoundManager {
    static let shared = SoundManager()
    private var isSoundEnabled: Bool = true
    
    func loadSettings() {
        isSoundEnabled = UserDefaults.standard.bool(forKey: "keyboard_sound_enabled")
    }
    
    func playKeySound() {
        guard isSoundEnabled else { return }
        UIDevice.current.playInputClick()
    }
    
    func setSoundEnabled(_ enabled: Bool) {
        isSoundEnabled = enabled
        UserDefaults.standard.set(enabled, forKey: "keyboard_sound_enabled")
    }
}
```

### 2.2 KeyboardLayout.swift
```swift
import Foundation
import UIKit

class KeyboardLayout {
    // QWERTY Layout
    static let qwertyRow1 = ["q", "w", "e", "r", "t", "y", "u", "i", "o", "p"]
    static let qwertyRow2 = ["a", "s", "d", "f", "g", "h", "j", "k", "l"]
    static let qwertyRow3 = ["z", "x", "c", "v", "b", "n", "m"]
    
    // Numbers Layout
    static let numbersRow1 = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "0"]
    static let numbersRow2 = ["-", "/", ":", ";", "(", ")", "$", "&", "@", "\""]
    static let numbersRow3 = [".", ",", "?", "!", "'", "\"", "-", "+", "="]
    
    // Symbols Layout
    static let symbolsRow1 = ["[", "]", "{", "}", "#", "%", "^", "*", "+", "="]
    static let symbolsRow2 = ["_", "\\", "|", "~", "<", ">", "€", "£", "¥", "•"]
    static let symbolsRow3 = [".", ",", "?", "!", "'", "\"", "-", "@", "$"]
    
    // Long press mappings
    static let longPressMap: [String: [String]] = [
        "a": ["à", "á", "â", "ä", "æ", "ã", "å", "ā"],
        "e": ["è", "é", "ê", "ë", "ē", "ė", "ę"],
        "i": ["ì", "í", "î", "ï", "ī", "į"],
        "o": ["ò", "ó", "ô", "ö", "õ", "ø", "ō", "œ"],
        "u": ["ù", "ú", "û", "ü", "ū"],
        "s": ["ß", "ś", "š"],
        "l": ["ł"],
        "n": ["ñ", "ń"],
        "c": ["ç", "ć", "č"],
        "z": ["ž", "ź", "ż"],
        "y": ["ý", "ÿ"],
        "0": ["°", "∅"],
        "1": ["¹", "½", "⅓", "¼", "⅛"],
        "2": ["²", "⅔"],
        "3": ["³", "¾", "⅜"],
        "4": ["⁴", "⅘"],
        "5": ["⁵", "⅝"],
        "6": ["⁶"],
        "7": ["⁷", "⅞"],
        "8": ["⁸"],
        "9": ["⁹"],
        ".": ["…"],
        "-": ["–", "—", "•"],
        "$": ["€", "£", "¥", "₩", "₹"],
        "&": ["§"],
        "\"": [""", """, "„", "»", "«"],
        "?": ["¿"],
        "!": ["¡"],
        "'": ["'", "'", "‚"],
        "%": ["‰"],
        "(": ["[", "{", "<"],
        ")": ["]", "}", ">"]
    ]
    
    static func createQwertyLayout() -> [[KeyboardKey]] {
        var layout: [[KeyboardKey]] = []
        
        // First row
        var row1: [KeyboardKey] = []
        for char in qwertyRow1 {
            let longPress = longPressMap[char]
            row1.append(KeyboardKey(
                type: .character(char),
                label: char,
                shiftLabel: char.uppercased(),
                longPressKeys: longPress
            ))
        }
        layout.append(row1)
        
        // Second row
        var row2: [KeyboardKey] = []
        for char in qwertyRow2 {
            let longPress = longPressMap[char]
            row2.append(KeyboardKey(
                type: .character(char),
                label: char,
                shiftLabel: char.uppercased(),
                longPressKeys: longPress
            ))
        }
        layout.append(row2)
        
        // Third row with shift and backspace
        var row3: [KeyboardKey] = []
        row3.append(KeyboardKey(
            type: .shift,
            label: "⇧",
            width: 1.5
        ))
        for char in qwertyRow3 {
            let longPress = longPressMap[char]
            row3.append(KeyboardKey(
                type: .character(char),
                label: char,
                shiftLabel: char.uppercased(),
                longPressKeys: longPress
            ))
        }
        row3.append(KeyboardKey(
            type: .backspace,
            label: "⌫",
            width: 1.5
        ))
        layout.append(row3)
        
        // Fourth row - bottom controls
        var row4: [KeyboardKey] = []
        row4.append(KeyboardKey(
            type: .numbers,
            label: "123",
            width: 1.5
        ))
        row4.append(KeyboardKey(
            type: .languageChange,
            label: "🌐",
            width: 1.0
        ))
        row4.append(KeyboardKey(
            type: .emoji,
            label: "😊",
            width: 1.0
        ))
        row4.append(KeyboardKey(
            type: .space,
            label: "space",
            width: 4.0
        ))
        row4.append(KeyboardKey(
            type: .character("."),
            label: ".",
            longPressKeys: [".", ",", "?", "!", "'", "\"", "-", "@"],
            width: 1.0
        ))
        row4.append(KeyboardKey(
            type: .enter,
            label: "↵",
            width: 1.5
        ))
        layout.append(row4)
        
        return layout
    }
    
    static func createNumbersLayout() -> [[KeyboardKey]] {
        var layout: [[KeyboardKey]] = []
        
        // First row - numbers
        var row1: [KeyboardKey] = []
        for char in numbersRow1 {
            let longPress = longPressMap[char]
            row1.append(KeyboardKey(
                type: .numberPad(char),
                label: char,
                longPressKeys: longPress
            ))
        }
        layout.append(row1)
        
        // Second row - symbols
        var row2: [KeyboardKey] = []
        for char in numbersRow2 {
            let longPress = longPressMap[char]
            row2.append(KeyboardKey(
                type: .numberPad(char),
                label: char,
                longPressKeys: longPress
            ))
        }
        layout.append(row2)
        
        // Third row
        var row3: [KeyboardKey] = []
        row3.append(KeyboardKey(
            type: .special("#+="),
            label: "#+=",
            width: 1.5
        ))
        for char in numbersRow3 {
            let longPress = longPressMap[char]
            row3.append(KeyboardKey(
                type: .numberPad(char),
                label: char,
                longPressKeys: longPress
            ))
        }
        row3.append(KeyboardKey(
            type: .backspace,
            label: "⌫",
            width: 1.5
        ))
        layout.append(row3)
        
        // Fourth row
        var row4: [KeyboardKey] = []
        row4.append(KeyboardKey(
            type: .character("ABC"),
            label: "ABC",
            width: 1.5
        ))
        row4.append(KeyboardKey(
            type: .languageChange,
            label: "🌐",
            width: 1.0
        ))
        row4.append(KeyboardKey(
            type: .emoji,
            label: "😊",
            width: 1.0
        ))
        row4.append(KeyboardKey(
            type: .space,
            label: "space",
            width: 4.0
        ))
        row4.append(KeyboardKey(
            type: .numberPad("."),
            label: ".",
            longPressKeys: [".", ",", "?", "!", "'", "\"", "-", "@"],
            width: 1.0
        ))
        row4.append(KeyboardKey(
            type: .enter,
            label: "↵",
            width: 1.5
        ))
        layout.append(row4)
        
        return layout
    }
    
    static func createSymbolsLayout() -> [[KeyboardKey]] {
        var layout: [[KeyboardKey]] = []
        
        // First row
        var row1: [KeyboardKey] = []
        for char in symbolsRow1 {
            row1.append(KeyboardKey(
                type: .symbolPad(char),
                label: char
            ))
        }
        layout.append(row1)
        
        // Second row
        var row2: [KeyboardKey] = []
        for char in symbolsRow2 {
            row2.append(KeyboardKey(
                type: .symbolPad(char),
                label: char
            ))
        }
        layout.append(row2)
        
        // Third row
        var row3: [KeyboardKey] = []
        row3.append(KeyboardKey(
            type: .numbers,
            label: "123",
            width: 1.5
        ))
        for char in symbolsRow3 {
            row3.append(KeyboardKey(
                type: .symbolPad(char),
                label: char
            ))
        }
        row3.append(KeyboardKey(
            type: .backspace,
            label: "⌫",
            width: 1.5
        ))
        layout.append(row3)
        
        // Fourth row
        var row4: [KeyboardKey] = []
        row4.append(KeyboardKey(
            type: .character("ABC"),
            label: "ABC",
            width: 1.5
        ))
        row4.append(KeyboardKey(
            type: .languageChange,
            label: "🌐",
            width: 1.0
        ))
        row4.append(KeyboardKey(
            type: .emoji,
            label: "😊",
            width: 1.0
        ))
        row4.append(KeyboardKey(
            type: .space,
            label: "space",
            width: 4.0
        ))
        row4.append(KeyboardKey(
            type: .symbolPad("."),
            label: ".",
            width: 1.0
        ))
        row4.append(KeyboardKey(
            type: .enter,
            label: "↵",
            width: 1.5
        ))
        layout.append(row4)
        
        return layout
    }
}
```

## 3. KeyboardViewController Implementation

### 3.1 KeyboardViewController.swift
```swift
import UIKit

class KeyboardViewController: UIInputViewController {
    
    // MARK: - Properties
    private var keyboardView: KeyboardView!
    private var currentLayout: [[KeyboardKey]] = []
    private var isShiftEnabled = false
    private var isCapsLockEnabled = false
    private var lastShiftTapTime: TimeInterval = 0
    private var currentKeyboardType: KeyboardType = .alphabetic
    private var heightConstraint: NSLayoutConstraint?
    private var longPressTimer: Timer?
    private var currentLongPressKey: KeyboardKey?
    private var popupView: KeyPopupView?
    private var isDarkMode = false
    
    enum KeyboardType {
        case alphabetic
        case numeric
        case symbols
    }
    
    // MARK: - View Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        setupKeyboard()
        loadUserPreferences()
        setupObservers()
        updateKeyboardAppearance()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        updateKeyboardHeight()
        updateKeyboardAppearance()
    }
    
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        super.viewWillTransition(to: size, with: coordinator)
        coordinator.animate(alongsideTransition: { _ in
            self.updateKeyboardHeight()
            self.keyboardView?.setNeedsLayout()
        })
    }
    
    override func textDidChange(_ textInput: UITextInput?) {
        super.textDidChange(textInput)
        updateShiftStateForNewSentence()
    }
    
    // MARK: - Setup Methods
    private func setupKeyboard() {
        // Initialize keyboard view
        keyboardView = KeyboardView(frame: .zero)
        keyboardView.translatesAutoresizingMaskIntoConstraints = false
        keyboardView.delegate = self
        view.addSubview(keyboardView)
        
        // Setup constraints
        NSLayoutConstraint.activate([
            keyboardView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            keyboardView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            keyboardView.topAnchor.constraint(equalTo: view.topAnchor),
            keyboardView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
        
        // Load initial layout
        currentLayout = KeyboardLayout.createQwertyLayout()
        keyboardView.setLayout(currentLayout)
        
        // Setup height constraint
        updateKeyboardHeight()
    }
    
    private func updateKeyboardHeight() {
        let isLandscape = UIScreen.main.bounds.width > UIScreen.main.bounds.height
        let keyboardHeight: CGFloat = isLandscape ? 200 : 260
        
        if let constraint = heightConstraint {
            constraint.constant = keyboardHeight
        } else {
            heightConstraint = view.heightAnchor.constraint(equalToConstant: keyboardHeight)
            heightConstraint?.priority = .required
            heightConstraint?.isActive = true
        }
    }
    
    private func loadUserPreferences() {
        let defaults = UserDefaults.standard
        isDarkMode = defaults.bool(forKey: "keyboard_dark_mode")
        SoundManager.shared.loadSettings()
        
        // Load vibration settings
        let vibrationEnabled = defaults.bool(forKey: "keyboard_vibration_enabled")
        if !vibrationEnabled {
            // Disable haptic feedback if needed
        }
    }
    
    private func setupObservers() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardWillShow),
            name: UIResponder.keyboardWillShowNotification,
            object: nil
        )
        
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardWillHide),
            name: UIResponder.keyboardWillHideNotification,
            object: nil
        )
    }
    
    @objc private func keyboardWillShow(_ notification: Notification) {
        updateKeyboardAppearance()
    }
    
    @objc private func keyboardWillHide(_ notification: Notification) {
        // Cleanup if needed
    }
    
    // MARK: - Appearance
    private func updateKeyboardAppearance() {
        let appearance = isDarkMode ? UIKeyboardAppearance.dark : UIKeyboardAppearance.light
        
        // Update background color
        view.backgroundColor = isDarkMode ? UIColor(white: 0.15, alpha: 1.0) : UIColor(white: 0.97, alpha: 1.0)
        
        // Update keyboard view appearance
        keyboardView?.updateAppearance(isDarkMode: isDarkMode)
    }
    
    // MARK: - Layout Switching
    private func switchToAlphabeticLayout() {
        currentKeyboardType = .alphabetic
        currentLayout = KeyboardLayout.createQwertyLayout()
        keyboardView.setLayout(currentLayout)
    }
    
    private func switchToNumericLayout() {
        currentKeyboardType = .numeric
        currentLayout = KeyboardLayout.createNumbersLayout()
        keyboardView.setLayout(currentLayout)
    }
    
    private func switchToSymbolsLayout() {
        currentKeyboardType = .symbols
        currentLayout = KeyboardLayout.createSymbolsLayout()
        keyboardView.setLayout(currentLayout)
    }
    
    // MARK: - Shift Handling
    private func handleShiftPress() {
        let currentTime = Date().timeIntervalSince1970
        
        if currentTime - lastShiftTapTime < 0.3 {
            // Double tap - enable caps lock
            isCapsLockEnabled = true
            isShiftEnabled = true
        } else {
            // Single tap - toggle shift
            if isCapsLockEnabled {
                isCapsLockEnabled = false
                isShiftEnabled = false
            } else {
                isShiftEnabled = !isShiftEnabled
            }
        }
        
        lastShiftTapTime = currentTime
        updateShiftState()
    }
    
    private func updateShiftState() {
        // Update all keys with shift state
        for row in currentLayout {
            for key in row {
                var updatedKey = key
                updatedKey.isShifted = isShiftEnabled
                // Update the key in the layout
            }
        }
        keyboardView.updateShiftState(isShiftEnabled, isCapsLock: isCapsLockEnabled)
    }
    
    private func updateShiftStateForNewSentence() {
        guard let proxy = textDocumentProxy else { return }
        
        // Check if we're at the beginning of a sentence
        let beforeText = proxy.documentContextBeforeInput ?? ""
        if beforeText.isEmpty || beforeText.hasSuffix(". ") || beforeText.hasSuffix("! ") || beforeText.hasSuffix("? ") {
            isShiftEnabled = true
            updateShiftState()
        }
    }
    
    // MARK: - Text Input
    private func insertText(_ text: String) {
        guard let proxy = textDocumentProxy else { return }
        
        let finalText = isShiftEnabled && !isCapsLockEnabled ? text.uppercased() : text
        proxy.insertText(finalText)
        
        // Auto-disable shift after character input (unless caps lock is on)
        if isShiftEnabled && !isCapsLockEnabled && currentKeyboardType == .alphabetic {
            isShiftEnabled = false
            updateShiftState()
        }
        
        // Play sound and haptic feedback
        SoundManager.shared.playKeySound()
        HapticManager.shared.triggerKeyPress()
    }
    
    private func handleBackspace() {
        guard let proxy = textDocumentProxy else { return }
        
        if proxy.hasText {
            proxy.deleteBackward()
        }
        
        SoundManager.shared.playKeySound()
        HapticManager.shared.triggerKeyPress()
    }
    
    private func handleSpace() {
        guard let proxy = textDocumentProxy else { return }
        
        // Check for double space for period insertion
        let beforeText = proxy.documentContextBeforeInput ?? ""
        if beforeText.hasSuffix(" ") && UserDefaults.standard.bool(forKey: "double_space_period") {
            proxy.deleteBackward()
            proxy.insertText(". ")
            isShiftEnabled = true
            updateShiftState()
        } else {
            proxy.insertText(" ")
        }
        
        SoundManager.shared.playKeySound()
        HapticManager.shared.triggerKeyPress()
    }
    
    private func handleReturn() {
        guard let proxy = textDocumentProxy else { return }
        proxy.insertText("\n")
        
        SoundManager.shared.playKeySound()
        HapticManager.shared.triggerKeyPress()
    }
    
    // MARK: - Long Press Handling
    private func startLongPress(for key: KeyboardKey) {
        guard let longPressKeys = key.longPressKeys, !longPressKeys.isEmpty else { return }
        
        currentLongPressKey = key
        
        // Start timer for long press detection
        longPressTimer = Timer.scheduledTimer(withTimeInterval: 0.3, repeats: false) { [weak self] _ in
            self?.showLongPressPopup(for: key, options: longPressKeys)
        }
    }
    
    private func endLongPress() {
        longPressTimer?.invalidate()
        longPressTimer = nil
        currentLongPressKey = nil
        popupView?.removeFromSuperview()
        popupView = nil
    }
    
    private func showLongPressPopup(for key: KeyboardKey, options: [String]) {
        // Create and show popup view
        popupView = KeyPopupView(options: options)
        popupView?.delegate = self
        
        if let popupView = popupView {
            view.addSubview(popupView)
            // Position popup above the key
            // Implementation depends on key position calculation
        }
        
        HapticManager.shared.triggerSelection()
    }
}

// MARK: - KeyboardViewDelegate
extension KeyboardViewController: KeyboardViewDelegate {
    func keyboardView(_ keyboardView: KeyboardView, didTapKey key: KeyboardKey) {
        switch key.type {
        case .character(let char):
            insertText(char)
        case .space:
            handleSpace()
        case .backspace:
            handleBackspace()
        case .enter:
            handleReturn()
        case .shift:
            handleShiftPress()
        case .numbers:
            if currentKeyboardType == .alphabetic {
                switchToNumericLayout()
            } else {
                switchToAlphabeticLayout()
            }
        case .special(let label):
            if label == "#+=" {
                switchToSymbolsLayout()
            }
        case .languageChange:
            advanceToNextInputMode()
        case .emoji:
            // Handle emoji keyboard
            break
        case .numberPad(let char), .symbolPad(let char):
            insertText(char)
        default:
            break
        }
    }
    
    func keyboardView(_ keyboardView: KeyboardView, didLongPressKey key: KeyboardKey) {
        startLongPress(for: key)
    }
    
    func keyboardView(_ keyboardView: KeyboardView, didEndLongPress key: KeyboardKey) {
        endLongPress()
    }
}

// MARK: - KeyPopupViewDelegate
extension KeyboardViewController: KeyPopupViewDelegate {
    func popupView(_ popupView: KeyPopupView, didSelectOption option: String) {
        insertText(option)
        endLongPress()
    }
}
```

## 4. Custom View Components

### 4.1 KeyboardView.swift
```swift
import UIKit

protocol KeyboardViewDelegate: AnyObject {
    func keyboardView(_ keyboardView: KeyboardView, didTapKey key: KeyboardKey)
    func keyboardView(_ keyboardView: KeyboardView, didLongPressKey key: KeyboardKey)
    func keyboardView(_ keyboardView: KeyboardView, didEndLongPress key: KeyboardKey)
}

class KeyboardView: UIView {
    
    weak var delegate: KeyboardViewDelegate?
    private var keyButtons: [[KeyButton]] = []
    private var layout: [[KeyboardKey]] = []
    private var isDarkMode: Bool = false
    private var stackView: UIStackView!
    private var keyboardWidth: CGFloat = 0
    private var keyboardHeight: CGFloat = 0
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupView()
    }
    
    private func setupView() {
        backgroundColor = .clear
        
        // Create main stack view
        stackView = UIStackView()
        stackView.axis = .vertical
        stackView.distribution = .fillEqually
        stackView.spacing = 6
        stackView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(stackView)
        
        NSLayoutConstraint.activate([
            stackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 3),
            stackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -3),
            stackView.topAnchor.constraint(equalTo: topAnchor, constant: 8),
            stackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -8)
        ])
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        keyboardWidth = bounds.width - 6
        keyboardHeight = bounds.height - 16
        updateKeyFrames()
    }
    
    func setLayout(_ newLayout: [[KeyboardKey]]) {
        layout = newLayout
        rebuildKeyboard()
    }
    
    private func rebuildKeyboard() {
        // Clear existing keys
        stackView.arrangedSubviews.forEach { $0.removeFromSuperview() }
        keyButtons.removeAll()
        
        // Build new keyboard
        for rowData in layout {
            let rowStackView = UIStackView()
            rowStackView.axis = .horizontal
            rowStackView.distribution = .fill
            rowStackView.spacing = 4
            
            var rowButtons: [KeyButton] = []
            
            for keyData in rowData {
                let button = KeyButton(key: keyData)
                button.delegate = self
                button.updateAppearance(isDarkMode: isDarkMode)
                rowButtons.append(button)
                rowStackView.addArrangedSubview(button)
                
                // Set width constraint based on key width
                let widthMultiplier = keyData.width
                if widthMultiplier != 1.0 {
                    button.widthAnchor.constraint(
                        equalTo: rowStackView.widthAnchor,
                        multiplier: widthMultiplier / 10.0
                    ).isActive = true
                }
            }
            
            keyButtons.append(rowButtons)
            stackView.addArrangedSubview(rowStackView)
        }
    }
    
    private func updateKeyFrames() {
        // Update frame calculations if needed
    }
    
    func updateAppearance(isDarkMode: Bool) {
        self.isDarkMode = isDarkMode
        
        for row in keyButtons {
            for button in row {
                button.updateAppearance(isDarkMode: isDarkMode)
            }
        }
    }
    
    func updateShiftState(_ isShifted: Bool, isCapsLock: Bool) {
        for row in keyButtons {
            for button in row {
                button.updateShiftState(isShifted, isCapsLock: isCapsLock)
            }
        }
    }
}

// MARK: - KeyButtonDelegate
extension KeyboardView: KeyButtonDelegate {
    func keyButton(_ button: KeyButton, didTapKey key: KeyboardKey) {
        delegate?.keyboardView(self, didTapKey: key)
    }
    
    func keyButton(_ button: KeyButton, didLongPressKey key: KeyboardKey) {
        delegate?.keyboardView(self, didLongPressKey: key)
    }
    
    func keyButton(_ button: KeyButton, didEndLongPress key: KeyboardKey) {
        delegate?.keyboardView(self, didEndLongPress: key)
    }
}
```