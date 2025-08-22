# iOS QWERTY Mini Wide Keyboard Implementation - Part 2: UI Components & Interactions

## 5. Key Button Implementation

### 5.1 KeyButton.swift
```swift
import UIKit

protocol KeyButtonDelegate: AnyObject {
    func keyButton(_ button: KeyButton, didTapKey key: KeyboardKey)
    func keyButton(_ button: KeyButton, didLongPressKey key: KeyboardKey)
    func keyButton(_ button: KeyButton, didEndLongPress key: KeyboardKey)
}

class KeyButton: UIControl {
    
    // MARK: - Properties
    weak var delegate: KeyButtonDelegate?
    private var key: KeyboardKey
    private let label = UILabel()
    private let backgroundView = UIView()
    private var isDarkMode = false
    private var isShifted = false
    private var isCapsLock = false
    private var longPressGesture: UILongPressGestureRecognizer!
    private var touchDownTime: TimeInterval = 0
    private var shadowLayer: CALayer?
    private var gradientLayer: CAGradientLayer?
    
    // Visual properties
    private struct Theme {
        // Light mode colors
        static let lightBackground = UIColor.white
        static let lightPressedBackground = UIColor(white: 0.85, alpha: 1.0)
        static let lightSpecialBackground = UIColor(white: 0.82, alpha: 1.0)
        static let lightSpecialPressedBackground = UIColor(white: 0.7, alpha: 1.0)
        static let lightTextColor = UIColor.black
        static let lightSpecialTextColor = UIColor.black
        static let lightBorderColor = UIColor(white: 0.9, alpha: 1.0)
        
        // Dark mode colors
        static let darkBackground = UIColor(white: 0.25, alpha: 1.0)
        static let darkPressedBackground = UIColor(white: 0.35, alpha: 1.0)
        static let darkSpecialBackground = UIColor(white: 0.18, alpha: 1.0)
        static let darkSpecialPressedBackground = UIColor(white: 0.28, alpha: 1.0)
        static let darkTextColor = UIColor.white
        static let darkSpecialTextColor = UIColor.white
        static let darkBorderColor = UIColor(white: 0.15, alpha: 1.0)
        
        // Common properties
        static let cornerRadius: CGFloat = 8
        static let shadowRadius: CGFloat = 2
        static let shadowOpacity: Float = 0.15
        static let borderWidth: CGFloat = 0.5
    }
    
    // MARK: - Initialization
    init(key: KeyboardKey) {
        self.key = key
        super.init(frame: .zero)
        setupView()
        setupGestures()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - Setup
    private func setupView() {
        // Background view setup
        backgroundView.isUserInteractionEnabled = false
        backgroundView.layer.cornerRadius = Theme.cornerRadius
        backgroundView.layer.masksToBounds = true
        addSubview(backgroundView)
        
        // Add shadow layer
        setupShadow()
        
        // Label setup
        label.textAlignment = .center
        label.adjustsFontSizeToFitWidth = true
        label.minimumScaleFactor = 0.5
        label.isUserInteractionEnabled = false
        addSubview(label)
        
        // Setup constraints
        backgroundView.translatesAutoresizingMaskIntoConstraints = false
        label.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            backgroundView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 2),
            backgroundView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -2),
            backgroundView.topAnchor.constraint(equalTo: topAnchor, constant: 2),
            backgroundView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -2),
            
            label.centerXAnchor.constraint(equalTo: centerXAnchor),
            label.centerYAnchor.constraint(equalTo: centerYAnchor)
        ])
        
        updateContent()
    }
    
    private func setupShadow() {
        shadowLayer = CALayer()
        shadowLayer?.shadowColor = UIColor.black.cgColor
        shadowLayer?.shadowOffset = CGSize(width: 0, height: 1)
        shadowLayer?.shadowRadius = Theme.shadowRadius
        shadowLayer?.shadowOpacity = Theme.shadowOpacity
        layer.insertSublayer(shadowLayer!, at: 0)
    }
    
    private func setupGestures() {
        // Long press gesture
        longPressGesture = UILongPressGestureRecognizer(target: self, action: #selector(handleLongPress(_:)))
        longPressGesture.minimumPressDuration = 0.3
        longPressGesture.cancelsTouchesInView = false
        addGestureRecognizer(longPressGesture)
        
        // Add target actions
        addTarget(self, action: #selector(touchDown), for: .touchDown)
        addTarget(self, action: #selector(touchUpInside), for: .touchUpInside)
        addTarget(self, action: #selector(touchUpOutside), for: [.touchUpOutside, .touchCancel])
        addTarget(self, action: #selector(touchDragExit), for: .touchDragExit)
        addTarget(self, action: #selector(touchDragEnter), for: .touchDragEnter)
    }
    
    // MARK: - Content Updates
    private func updateContent() {
        // Update label text
        switch key.type {
        case .character(let char):
            label.text = isShifted ? char.uppercased() : char
            configureFontSize(for: .regular)
        case .space:
            label.text = "space"
            configureFontSize(for: .small)
        case .backspace:
            label.text = "⌫"
            configureFontSize(for: .icon)
        case .enter:
            label.text = "return"
            configureFontSize(for: .small)
        case .shift:
            updateShiftIcon()
            configureFontSize(for: .icon)
        case .numbers:
            label.text = key.label
            configureFontSize(for: .small)
        case .languageChange:
            label.text = "🌐"
            configureFontSize(for: .icon)
        case .emoji:
            label.text = "😊"
            configureFontSize(for: .icon)
        case .special(let text):
            label.text = text
            configureFontSize(for: .small)
        case .numberPad(let char), .symbolPad(let char):
            label.text = char
            configureFontSize(for: .regular)
        default:
            label.text = key.label
            configureFontSize(for: .regular)
        }
        
        updateColors()
    }
    
    private func configureFontSize(for type: FontSizeType) {
        switch type {
        case .regular:
            label.font = .systemFont(ofSize: 22, weight: .regular)
        case .small:
            label.font = .systemFont(ofSize: 16, weight: .medium)
        case .icon:
            label.font = .systemFont(ofSize: 20, weight: .regular)
        }
    }
    
    private enum FontSizeType {
        case regular, small, icon
    }
    
    private func updateShiftIcon() {
        if isCapsLock {
            label.text = "⇪"  // Caps lock icon
        } else if isShifted {
            label.text = "⇧"  // Filled shift arrow
        } else {
            label.text = "⇧"  // Outline shift arrow
        }
    }
    
    // MARK: - Appearance
    func updateAppearance(isDarkMode: Bool) {
        self.isDarkMode = isDarkMode
        updateColors()
    }
    
    private func updateColors() {
        let isSpecialKey = isSpecialKey()
        
        if isDarkMode {
            backgroundView.backgroundColor = isSpecialKey ? Theme.darkSpecialBackground : Theme.darkBackground
            label.textColor = isSpecialKey ? Theme.darkSpecialTextColor : Theme.darkTextColor
            backgroundView.layer.borderColor = Theme.darkBorderColor.cgColor
        } else {
            backgroundView.backgroundColor = isSpecialKey ? Theme.lightSpecialBackground : Theme.lightBackground
            label.textColor = isSpecialKey ? Theme.lightSpecialTextColor : Theme.lightTextColor
            backgroundView.layer.borderColor = Theme.lightBorderColor.cgColor
        }
        
        backgroundView.layer.borderWidth = Theme.borderWidth
        shadowLayer?.shadowColor = isDarkMode ? UIColor.black.cgColor : UIColor.gray.cgColor
    }
    
    private func isSpecialKey() -> Bool {
        switch key.type {
        case .shift, .backspace, .numbers, .enter, .special, .languageChange:
            return true
        default:
            return false
        }
    }
    
    func updateShiftState(_ isShifted: Bool, isCapsLock: Bool) {
        self.isShifted = isShifted
        self.isCapsLock = isCapsLock
        updateContent()
    }
    
    // MARK: - Touch Handling
    @objc private func touchDown() {
        touchDownTime = Date().timeIntervalSince1970
        animatePress(true)
        HapticManager.shared.triggerKeyPress()
    }
    
    @objc private func touchUpInside() {
        animatePress(false)
        
        // Check if this was a quick tap (not a long press)
        let touchDuration = Date().timeIntervalSince1970 - touchDownTime
        if touchDuration < 0.3 {
            delegate?.keyButton(self, didTapKey: key)
        }
    }
    
    @objc private func touchUpOutside() {
        animatePress(false)
    }
    
    @objc private func touchDragExit() {
        animatePress(false)
    }
    
    @objc private func touchDragEnter() {
        animatePress(true)
    }
    
    @objc private func handleLongPress(_ gesture: UILongPressGestureRecognizer) {
        switch gesture.state {
        case .began:
            delegate?.keyButton(self, didLongPressKey: key)
            HapticManager.shared.triggerSelection()
        case .ended, .cancelled:
            delegate?.keyButton(self, didEndLongPress: key)
        default:
            break
        }
    }
    
    // MARK: - Animations
    private func animatePress(_ pressed: Bool) {
        let isSpecial = isSpecialKey()
        
        UIView.animate(withDuration: 0.1, delay: 0, options: [.allowUserInteraction, .curveEaseOut]) {
            if pressed {
                self.transform = CGAffineTransform(scaleX: 0.95, y: 0.95)
                if self.isDarkMode {
                    self.backgroundView.backgroundColor = isSpecial ? Theme.darkSpecialPressedBackground : Theme.darkPressedBackground
                } else {
                    self.backgroundView.backgroundColor = isSpecial ? Theme.lightSpecialPressedBackground : Theme.lightPressedBackground
                }
                self.shadowLayer?.shadowOpacity = 0.05
            } else {
                self.transform = .identity
                if self.isDarkMode {
                    self.backgroundView.backgroundColor = isSpecial ? Theme.darkSpecialBackground : Theme.darkBackground
                } else {
                    self.backgroundView.backgroundColor = isSpecial ? Theme.lightSpecialBackground : Theme.lightBackground
                }
                self.shadowLayer?.shadowOpacity = Theme.shadowOpacity
            }
        }
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        shadowLayer?.frame = backgroundView.frame
        shadowLayer?.shadowPath = UIBezierPath(roundedRect: backgroundView.bounds, cornerRadius: Theme.cornerRadius).cgPath
    }
}

// MARK: - KeyPopupView
class KeyPopupView: UIView {
    
    weak var delegate: KeyPopupViewDelegate?
    private var options: [String]
    private var buttons: [UIButton] = []
    private var stackView: UIStackView!
    private var backgroundView: UIView!
    private var isDarkMode: Bool = false
    
    init(options: [String]) {
        self.options = options
        super.init(frame: .zero)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupView() {
        // Background blur effect
        let blurEffect = UIBlurEffect(style: .systemMaterial)
        let blurView = UIVisualEffectView(effect: blurEffect)
        blurView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(blurView)
        
        // Background view
        backgroundView = UIView()
        backgroundView.backgroundColor = UIColor.systemBackground.withAlphaComponent(0.9)
        backgroundView.layer.cornerRadius = 12
        backgroundView.layer.shadowColor = UIColor.black.cgColor
        backgroundView.layer.shadowOffset = CGSize(width: 0, height: 2)
        backgroundView.layer.shadowRadius = 8
        backgroundView.layer.shadowOpacity = 0.2
        backgroundView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(backgroundView)
        
        // Stack view for options
        stackView = UIStackView()
        stackView.axis = .horizontal
        stackView.distribution = .fillEqually
        stackView.spacing = 4
        stackView.translatesAutoresizingMaskIntoConstraints = false
        backgroundView.addSubview(stackView)
        
        // Create option buttons
        for option in options {
            let button = UIButton(type: .system)
            button.setTitle(option, for: .normal)
            button.titleLabel?.font = .systemFont(ofSize: 20)
            button.addTarget(self, action: #selector(optionTapped(_:)), for: .touchUpInside)
            buttons.append(button)
            stackView.addArrangedSubview(button)
        }
        
        // Setup constraints
        NSLayoutConstraint.activate([
            blurView.leadingAnchor.constraint(equalTo: leadingAnchor),
            blurView.trailingAnchor.constraint(equalTo: trailingAnchor),
            blurView.topAnchor.constraint(equalTo: topAnchor),
            blurView.bottomAnchor.constraint(equalTo: bottomAnchor),
            
            backgroundView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 8),
            backgroundView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -8),
            backgroundView.topAnchor.constraint(equalTo: topAnchor, constant: 8),
            backgroundView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -8),
            
            stackView.leadingAnchor.constraint(equalTo: backgroundView.leadingAnchor, constant: 12),
            stackView.trailingAnchor.constraint(equalTo: backgroundView.trailingAnchor, constant: -12),
            stackView.topAnchor.constraint(equalTo: backgroundView.topAnchor, constant: 12),
            stackView.bottomAnchor.constraint(equalTo: backgroundView.bottomAnchor, constant: -12)
        ])
    }
    
    @objc private func optionTapped(_ sender: UIButton) {
        guard let title = sender.title(for: .normal) else { return }
        delegate?.popupView(self, didSelectOption: title)
        HapticManager.shared.triggerSelection()
    }
    
    func show(above keyFrame: CGRect, in parentView: UIView) {
        parentView.addSubview(self)
        translatesAutoresizingMaskIntoConstraints = false
        
        // Calculate popup size
        let popupWidth = CGFloat(options.count * 44 + (options.count - 1) * 4 + 32)
        let popupHeight: CGFloat = 60
        
        NSLayoutConstraint.activate([
            widthAnchor.constraint(equalToConstant: popupWidth),
            heightAnchor.constraint(equalToConstant: popupHeight),
            centerXAnchor.constraint(equalTo: parentView.leadingAnchor, constant: keyFrame.midX),
            bottomAnchor.constraint(equalTo: parentView.topAnchor, constant: keyFrame.minY - 8)
        ])
        
        // Animate appearance
        alpha = 0
        transform = CGAffineTransform(scaleX: 0.8, y: 0.8)
        
        UIView.animate(withDuration: 0.2, delay: 0, usingSpringWithDamping: 0.8, initialSpringVelocity: 0.5, options: .curveEaseOut) {
            self.alpha = 1
            self.transform = .identity
        }
    }
}

protocol KeyPopupViewDelegate: AnyObject {
    func popupView(_ popupView: KeyPopupView, didSelectOption option: String)
}
```

## 6. Settings & Configuration

### 6.1 SettingsViewController.swift
```swift
import UIKit

class SettingsViewController: UIViewController {
    
    // MARK: - UI Components
    private let tableView = UITableView(frame: .zero, style: .insetGrouped)
    private var settings: [SettingSection] = []
    
    struct SettingSection {
        let title: String
        let items: [SettingItem]
    }
    
    struct SettingItem {
        let title: String
        let type: SettingType
        let key: String
        var value: Any?
    }
    
    enum SettingType {
        case toggle
        case selection
        case slider
        case action
    }
    
    // MARK: - Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        loadSettings()
    }
    
    private func setupUI() {
        title = "Keyboard Settings"
        view.backgroundColor = .systemBackground
        
        // Navigation bar
        navigationItem.rightBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .done,
            target: self,
            action: #selector(dismissSettings)
        )
        
        // Table view setup
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(ToggleCell.self, forCellReuseIdentifier: "ToggleCell")
        tableView.register(SelectionCell.self, forCellReuseIdentifier: "SelectionCell")
        tableView.register(SliderCell.self, forCellReuseIdentifier: "SliderCell")
        tableView.register(ActionCell.self, forCellReuseIdentifier: "ActionCell")
        tableView.translatesAutoresizingMaskIntoConstraints = false
        
        view.addSubview(tableView)
        
        NSLayoutConstraint.activate([
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            tableView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            tableView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
    }
    
    private func loadSettings() {
        let defaults = UserDefaults.standard
        
        settings = [
            SettingSection(title: "Appearance", items: [
                SettingItem(
                    title: "Dark Mode",
                    type: .toggle,
                    key: "keyboard_dark_mode",
                    value: defaults.bool(forKey: "keyboard_dark_mode")
                ),
                SettingItem(
                    title: "Key Height",
                    type: .slider,
                    key: "keyboard_key_height",
                    value: defaults.float(forKey: "keyboard_key_height")
                )
            ]),
            
            SettingSection(title: "Feedback", items: [
                SettingItem(
                    title: "Sound",
                    type: .toggle,
                    key: "keyboard_sound_enabled",
                    value: defaults.bool(forKey: "keyboard_sound_enabled")
                ),
                SettingItem(
                    title: "Vibration",
                    type: .toggle,
                    key: "keyboard_vibration_enabled",
                    value: defaults.bool(forKey: "keyboard_vibration_enabled")
                ),
                SettingItem(
                    title: "Vibration Intensity",
                    type: .slider,
                    key: "keyboard_vibration_intensity",
                    value: defaults.float(forKey: "keyboard_vibration_intensity")
                )
            ]),
            
            SettingSection(title: "Typing", items: [
                SettingItem(
                    title: "Auto-Capitalization",
                    type: .toggle,
                    key: "auto_capitalization",
                    value: defaults.bool(forKey: "auto_capitalization")
                ),
                SettingItem(
                    title: "Auto-Correction",
                    type: .toggle,
                    key: "auto_correction",
                    value: defaults.bool(forKey: "auto_correction")
                ),
                SettingItem(
                    title: "Double Space for Period",
                    type: .toggle,
                    key: "double_space_period",
                    value: defaults.bool(forKey: "double_space_period")
                ),
                SettingItem(
                    title: "Long Press Delay",
                    type: .slider,
                    key: "long_press_delay",
                    value: defaults.float(forKey: "long_press_delay")
                )
            ]),
            
            SettingSection(title: "Advanced", items: [
                SettingItem(
                    title: "Key Preview",
                    type: .toggle,
                    key: "key_preview_enabled",
                    value: defaults.bool(forKey: "key_preview_enabled")
                ),
                SettingItem(
                    title: "Swipe to Type",
                    type: .toggle,
                    key: "swipe_typing_enabled",
                    value: defaults.bool(forKey: "swipe_typing_enabled")
                ),
                SettingItem(
                    title: "Reset to Defaults",
                    type: .action,
                    key: "reset_defaults",
                    value: nil
                )
            ])
        ]
        
        tableView.reloadData()
    }
    
    @objc private func dismissSettings() {
        dismiss(animated: true)
    }
    
    private func saveSetting(key: String, value: Any) {
        UserDefaults.standard.set(value, forKey: key)
        UserDefaults.standard.synchronize()
        
        // Notify keyboard of changes
        NotificationCenter.default.post(
            name: Notification.Name("SettingsChanged"),
            object: nil,
            userInfo: ["key": key, "value": value]
        )
    }
    
    private func resetToDefaults() {
        let alert = UIAlertController(
            title: "Reset Settings",
            message: "Are you sure you want to reset all settings to defaults?",
            preferredStyle: .alert
        )
        
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
        alert.addAction(UIAlertAction(title: "Reset", style: .destructive) { _ in
            self.performReset()
        })
        
        present(alert, animated: true)
    }
    
    private func performReset() {
        let defaults = UserDefaults.standard
        let keys = [
            "keyboard_dark_mode",
            "keyboard_sound_enabled",
            "keyboard_vibration_enabled",
            "keyboard_vibration_intensity",
            "auto_capitalization",
            "auto_correction",
            "double_space_period",
            "long_press_delay",
            "key_preview_enabled",
            "swipe_typing_enabled",
            "keyboard_key_height"
        ]
        
        for key in keys {
            defaults.removeObject(forKey: key)
        }
        
        defaults.synchronize()
        loadSettings()
    }
}

// MARK: - UITableViewDataSource
extension SettingsViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return settings.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return settings[section].items.count
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return settings[section].title
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let item = settings[indexPath.section].items[indexPath.row]
        
        switch item.type {
        case .toggle:
            let cell = tableView.dequeueReusableCell(withIdentifier: "ToggleCell", for: indexPath) as! ToggleCell
            cell.configure(with: item)
            cell.onToggle = { [weak self] isOn in
                self?.saveSetting(key: item.key, value: isOn)
            }
            return cell
            
        case .selection:
            let cell = tableView.dequeueReusableCell(withIdentifier: "SelectionCell", for: indexPath) as! SelectionCell
            cell.configure(with: item)
            return cell
            
        case .slider:
            let cell = tableView.dequeueReusableCell(withIdentifier: "SliderCell", for: indexPath) as! SliderCell
            cell.configure(with: item)
            cell.onValueChange = { [weak self] value in
                self?.saveSetting(key: item.key, value: value)
            }
            return cell
            
        case .action:
            let cell = tableView.dequeueReusableCell(withIdentifier: "ActionCell", for: indexPath) as! ActionCell
            cell.configure(with: item)
            return cell
        }
    }
}

// MARK: - UITableViewDelegate
extension SettingsViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        let item = settings[indexPath.section].items[indexPath.row]
        
        switch item.type {
        case .action:
            if item.key == "reset_defaults" {
                resetToDefaults()
            }
        case .selection:
            // Handle selection type settings
            break
        default:
            break
        }
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        let item = settings[indexPath.section].items[indexPath.row]
        return item.type == .slider ? 80 : 44
    }
}

// MARK: - Custom Cell Classes
class ToggleCell: UITableViewCell {
    private let toggle = UISwitch()
    var onToggle: ((Bool) -> Void)?
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupCell()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupCell()
    }
    
    private func setupCell() {
        selectionStyle = .none
        toggle.addTarget(self, action: #selector(toggleChanged), for: .valueChanged)
        accessoryView = toggle
    }
    
    func configure(with item: SettingsViewController.SettingItem) {
        textLabel?.text = item.title
        toggle.isOn = item.value as? Bool ?? false
    }
    
    @objc private func toggleChanged() {
        onToggle?(toggle.isOn)
    }
}

class SliderCell: UITableViewCell {
    private let slider = UISlider()
    private let valueLabel = UILabel()
    var onValueChange: ((Float) -> Void)?
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: .default, reuseIdentifier: reuseIdentifier)
        setupCell()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupCell()
    }
    
    private func setupCell() {
        selectionStyle = .none
        
        slider.minimumValue = 0
        slider.maximumValue = 1
        slider.addTarget(self, action: #selector(sliderChanged), for: .valueChanged)
        slider.translatesAutoresizingMaskIntoConstraints = false
        
        valueLabel.textAlignment = .right
        valueLabel.font = .systemFont(ofSize: 14)
        valueLabel.textColor = .secondaryLabel
        valueLabel.translatesAutoresizingMaskIntoConstraints = false
        
        contentView.addSubview(slider)
        contentView.addSubview(valueLabel)
        
        NSLayoutConstraint.activate([
            slider.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            slider.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            slider.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -8),
            
            valueLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            valueLabel.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 12)
        ])
    }
    
    func configure(with item: SettingsViewController.SettingItem) {
        textLabel?.text = item.title
        slider.value = item.value as? Float ?? 0.5
        updateValueLabel()
    }
    
    @objc private func sliderChanged() {
        updateValueLabel()
        onValueChange?(slider.value)
    }
    
    private func updateValueLabel() {
        valueLabel.text = String(format: "%.0f%%", slider.value * 100)
    }
}

class SelectionCell: UITableViewCell {
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: .value1, reuseIdentifier: reuseIdentifier)
        accessoryType = .disclosureIndicator
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    func configure(with item: SettingsViewController.SettingItem) {
        textLabel?.text = item.title
        detailTextLabel?.text = item.value as? String ?? "Default"
    }
}

class ActionCell: UITableViewCell {
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        textLabel?.textColor = .systemBlue
        textLabel?.textAlignment = .center
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    func configure(with item: SettingsViewController.SettingItem) {
        textLabel?.text = item.title
    }
}
```