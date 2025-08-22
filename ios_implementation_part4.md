# iOS QWERTY Mini Wide Keyboard Implementation - Part 4: Main App, Accessibility & Extensions

## 10. Main Application

### 10.1 AppDelegate.swift
```swift
import UIKit

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        setupDefaultSettings()
        setupAppearance()
        registerForNotifications()
        return true
    }
    
    private func setupDefaultSettings() {
        let defaults = UserDefaults.standard
        
        // Set default values if not already set
        let defaultSettings: [String: Any] = [
            "keyboard_dark_mode": false,
            "keyboard_sound_enabled": true,
            "keyboard_vibration_enabled": true,
            "keyboard_vibration_intensity": 0.5,
            "auto_capitalization": true,
            "auto_correction": true,
            "double_space_period": true,
            "long_press_delay": 0.3,
            "key_preview_enabled": true,
            "swipe_typing_enabled": false,
            "keyboard_key_height": 1.0,
            "predictive_text_enabled": true,
            "emoji_suggestions_enabled": true,
            "keyboard_click_enabled": true,
            "keyboard_transparency": 1.0,
            "key_press_animation": true,
            "keyboard_layout": "qwerty",
            "last_used_language": "en",
            "custom_words_enabled": true,
            "cloud_sync_enabled": false
        ]
        
        for (key, value) in defaultSettings {
            if defaults.object(forKey: key) == nil {
                defaults.set(value, forKey: key)
            }
        }
        
        defaults.synchronize()
    }
    
    private func setupAppearance() {
        // Setup global appearance
        UINavigationBar.appearance().tintColor = .systemBlue
        UITabBar.appearance().tintColor = .systemBlue
        
        // Set up dynamic colors for dark mode support
        if #available(iOS 13.0, *) {
            let window = UIApplication.shared.windows.first
            window?.overrideUserInterfaceStyle = .unspecified
        }
    }
    
    private func registerForNotifications() {
        // Register for keyboard change notifications
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardSettingsChanged),
            name: Notification.Name("KeyboardSettingsChanged"),
            object: nil
        )
    }
    
    @objc private func keyboardSettingsChanged(_ notification: Notification) {
        // Handle keyboard settings changes
        if let userInfo = notification.userInfo,
           let key = userInfo["key"] as? String,
           let value = userInfo["value"] {
            print("Setting changed: \(key) = \(value)")
        }
    }

    // MARK: UISceneSession Lifecycle

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
    }
}
```

### 10.2 SceneDelegate.swift
```swift
import UIKit

class SceneDelegate: UIResponder, UIWindowSceneDelegate {

    var window: UIWindow?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        guard let windowScene = (scene as? UIWindowScene) else { return }
        
        window = UIWindow(windowScene: windowScene)
        let mainViewController = MainViewController()
        let navigationController = UINavigationController(rootViewController: mainViewController)
        window?.rootViewController = navigationController
        window?.makeKeyAndVisible()
    }

    func sceneDidDisconnect(_ scene: UIScene) {
    }

    func sceneDidBecomeActive(_ scene: UIScene) {
    }

    func sceneWillResignActive(_ scene: UIScene) {
    }

    func sceneWillEnterForeground(_ scene: UIScene) {
    }

    func sceneDidEnterBackground(_ scene: UIScene) {
    }
}
```

### 10.3 MainViewController.swift
```swift
import UIKit

class MainViewController: UIViewController {
    
    // MARK: - UI Components
    private let scrollView = UIScrollView()
    private let contentView = UIView()
    private let headerView = UIView()
    private let titleLabel = UILabel()
    private let subtitleLabel = UILabel()
    private let featureStackView = UIStackView()
    private let settingsButton = UIButton(type: .system)
    private let enableKeyboardButton = UIButton(type: .system)
    private let tutorialButton = UIButton(type: .system)
    private let feedbackButton = UIButton(type: .system)
    
    // MARK: - Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        checkKeyboardStatus()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        checkKeyboardStatus()
    }
    
    // MARK: - UI Setup
    private func setupUI() {
        view.backgroundColor = .systemBackground
        navigationItem.title = "QWERTY Mini Wide"
        
        // Setup scroll view
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(scrollView)
        
        contentView.translatesAutoresizingMaskIntoConstraints = false
        scrollView.addSubview(contentView)
        
        // Setup header
        setupHeaderView()
        
        // Setup feature cards
        setupFeatureCards()
        
        // Setup action buttons
        setupActionButtons()
        
        // Setup constraints
        setupConstraints()
    }
    
    private func setupHeaderView() {
        headerView.backgroundColor = .systemBlue
        headerView.layer.cornerRadius = 20
        headerView.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(headerView)
        
        titleLabel.text = "QWERTY Mini Wide"
        titleLabel.font = .systemFont(ofSize: 28, weight: .bold)
        titleLabel.textColor = .white
        titleLabel.textAlignment = .center
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        headerView.addSubview(titleLabel)
        
        subtitleLabel.text = "Professional Keyboard for iOS"
        subtitleLabel.font = .systemFont(ofSize: 16, weight: .medium)
        subtitleLabel.textColor = .white.withAlphaComponent(0.9)
        subtitleLabel.textAlignment = .center
        subtitleLabel.translatesAutoresizingMaskIntoConstraints = false
        headerView.addSubview(subtitleLabel)
    }
    
    private func setupFeatureCards() {
        featureStackView.axis = .vertical
        featureStackView.spacing = 16
        featureStackView.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(featureStackView)
        
        // Create feature cards
        let features = [
            ("⌨️", "Multiple Layouts", "QWERTY, Numbers, Symbols, and Emoji keyboards"),
            ("🎨", "Customizable", "Dark mode, key height, and vibration settings"),
            ("⚡", "Fast Typing", "Auto-correction, predictive text, and swipe typing"),
            ("🌍", "Multi-Language", "Support for multiple languages and special characters"),
            ("🔒", "Privacy First", "No data collection, works offline"),
            ("♿", "Accessibility", "VoiceOver support and adjustable key sizes")
        ]
        
        for feature in features {
            let card = createFeatureCard(icon: feature.0, title: feature.1, description: feature.2)
            featureStackView.addArrangedSubview(card)
        }
    }
    
    private func createFeatureCard(icon: String, title: String, description: String) -> UIView {
        let cardView = UIView()
        cardView.backgroundColor = .secondarySystemBackground
        cardView.layer.cornerRadius = 12
        cardView.layer.shadowColor = UIColor.black.cgColor
        cardView.layer.shadowOffset = CGSize(width: 0, height: 2)
        cardView.layer.shadowRadius = 4
        cardView.layer.shadowOpacity = 0.1
        
        let iconLabel = UILabel()
        iconLabel.text = icon
        iconLabel.font = .systemFont(ofSize: 32)
        iconLabel.translatesAutoresizingMaskIntoConstraints = false
        cardView.addSubview(iconLabel)
        
        let titleLabel = UILabel()
        titleLabel.text = title
        titleLabel.font = .systemFont(ofSize: 18, weight: .semibold)
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        cardView.addSubview(titleLabel)
        
        let descriptionLabel = UILabel()
        descriptionLabel.text = description
        descriptionLabel.font = .systemFont(ofSize: 14)
        descriptionLabel.textColor = .secondaryLabel
        descriptionLabel.numberOfLines = 0
        descriptionLabel.translatesAutoresizingMaskIntoConstraints = false
        cardView.addSubview(descriptionLabel)
        
        NSLayoutConstraint.activate([
            cardView.heightAnchor.constraint(greaterThanOrEqualToConstant: 80),
            
            iconLabel.leadingAnchor.constraint(equalTo: cardView.leadingAnchor, constant: 16),
            iconLabel.centerYAnchor.constraint(equalTo: cardView.centerYAnchor),
            
            titleLabel.leadingAnchor.constraint(equalTo: iconLabel.trailingAnchor, constant: 16),
            titleLabel.topAnchor.constraint(equalTo: cardView.topAnchor, constant: 16),
            titleLabel.trailingAnchor.constraint(equalTo: cardView.trailingAnchor, constant: -16),
            
            descriptionLabel.leadingAnchor.constraint(equalTo: titleLabel.leadingAnchor),
            descriptionLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 4),
            descriptionLabel.trailingAnchor.constraint(equalTo: titleLabel.trailingAnchor),
            descriptionLabel.bottomAnchor.constraint(equalTo: cardView.bottomAnchor, constant: -16)
        ])
        
        return cardView
    }
    
    private func setupActionButtons() {
        // Enable Keyboard Button
        enableKeyboardButton.setTitle("Enable Keyboard", for: .normal)
        enableKeyboardButton.titleLabel?.font = .systemFont(ofSize: 18, weight: .semibold)
        enableKeyboardButton.backgroundColor = .systemGreen
        enableKeyboardButton.setTitleColor(.white, for: .normal)
        enableKeyboardButton.layer.cornerRadius = 12
        enableKeyboardButton.addTarget(self, action: #selector(enableKeyboardTapped), for: .touchUpInside)
        enableKeyboardButton.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(enableKeyboardButton)
        
        // Settings Button
        settingsButton.setTitle("⚙️ Settings", for: .normal)
        settingsButton.titleLabel?.font = .systemFont(ofSize: 16, weight: .medium)
        settingsButton.backgroundColor = .secondarySystemBackground
        settingsButton.setTitleColor(.label, for: .normal)
        settingsButton.layer.cornerRadius = 12
        settingsButton.addTarget(self, action: #selector(settingsTapped), for: .touchUpInside)
        settingsButton.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(settingsButton)
        
        // Tutorial Button
        tutorialButton.setTitle("📖 Tutorial", for: .normal)
        tutorialButton.titleLabel?.font = .systemFont(ofSize: 16, weight: .medium)
        tutorialButton.backgroundColor = .secondarySystemBackground
        tutorialButton.setTitleColor(.label, for: .normal)
        tutorialButton.layer.cornerRadius = 12
        tutorialButton.addTarget(self, action: #selector(tutorialTapped), for: .touchUpInside)
        tutorialButton.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(tutorialButton)
        
        // Feedback Button
        feedbackButton.setTitle("💬 Feedback", for: .normal)
        feedbackButton.titleLabel?.font = .systemFont(ofSize: 16, weight: .medium)
        feedbackButton.backgroundColor = .secondarySystemBackground
        feedbackButton.setTitleColor(.label, for: .normal)
        feedbackButton.layer.cornerRadius = 12
        feedbackButton.addTarget(self, action: #selector(feedbackTapped), for: .touchUpInside)
        feedbackButton.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(feedbackButton)
    }
    
    private func setupConstraints() {
        NSLayoutConstraint.activate([
            // Scroll view
            scrollView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            scrollView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            
            // Content view
            contentView.leadingAnchor.constraint(equalTo: scrollView.leadingAnchor),
            contentView.trailingAnchor.constraint(equalTo: scrollView.trailingAnchor),
            contentView.topAnchor.constraint(equalTo: scrollView.topAnchor),
            contentView.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
            contentView.widthAnchor.constraint(equalTo: scrollView.widthAnchor),
            
            // Header view
            headerView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            headerView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            headerView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 20),
            headerView.heightAnchor.constraint(equalToConstant: 120),
            
            // Title and subtitle
            titleLabel.centerXAnchor.constraint(equalTo: headerView.centerXAnchor),
            titleLabel.topAnchor.constraint(equalTo: headerView.topAnchor, constant: 30),
            
            subtitleLabel.centerXAnchor.constraint(equalTo: headerView.centerXAnchor),
            subtitleLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 8),
            
            // Feature stack view
            featureStackView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            featureStackView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            featureStackView.topAnchor.constraint(equalTo: headerView.bottomAnchor, constant: 30),
            
            // Enable keyboard button
            enableKeyboardButton.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            enableKeyboardButton.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            enableKeyboardButton.topAnchor.constraint(equalTo: featureStackView.bottomAnchor, constant: 30),
            enableKeyboardButton.heightAnchor.constraint(equalToConstant: 56),
            
            // Settings button
            settingsButton.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            settingsButton.trailingAnchor.constraint(equalTo: contentView.centerXAnchor, constant: -8),
            settingsButton.topAnchor.constraint(equalTo: enableKeyboardButton.bottomAnchor, constant: 16),
            settingsButton.heightAnchor.constraint(equalToConstant: 48),
            
            // Tutorial button
            tutorialButton.leadingAnchor.constraint(equalTo: contentView.centerXAnchor, constant: 8),
            tutorialButton.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            tutorialButton.topAnchor.constraint(equalTo: enableKeyboardButton.bottomAnchor, constant: 16),
            tutorialButton.heightAnchor.constraint(equalToConstant: 48),
            
            // Feedback button
            feedbackButton.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            feedbackButton.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            feedbackButton.topAnchor.constraint(equalTo: settingsButton.bottomAnchor, constant: 16),
            feedbackButton.heightAnchor.constraint(equalToConstant: 48),
            feedbackButton.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -30)
        ])
    }
    
    // MARK: - Actions
    @objc private func enableKeyboardTapped() {
        // Open Settings app to keyboard section
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
        }
        
        // Show instructions
        showEnableInstructions()
    }
    
    @objc private func settingsTapped() {
        let settingsVC = SettingsViewController()
        navigationController?.pushViewController(settingsVC, animated: true)
    }
    
    @objc private func tutorialTapped() {
        let tutorialVC = TutorialViewController()
        navigationController?.pushViewController(tutorialVC, animated: true)
    }
    
    @objc private func feedbackTapped() {
        let feedbackVC = FeedbackViewController()
        navigationController?.pushViewController(feedbackVC, animated: true)
    }
    
    // MARK: - Helper Methods
    private func checkKeyboardStatus() {
        // Check if keyboard is enabled
        let isKeyboardEnabled = checkIfKeyboardEnabled()
        
        if isKeyboardEnabled {
            enableKeyboardButton.setTitle("✓ Keyboard Enabled", for: .normal)
            enableKeyboardButton.backgroundColor = .systemGray
            enableKeyboardButton.isEnabled = false
        } else {
            enableKeyboardButton.setTitle("Enable Keyboard", for: .normal)
            enableKeyboardButton.backgroundColor = .systemGreen
            enableKeyboardButton.isEnabled = true
        }
    }
    
    private func checkIfKeyboardEnabled() -> Bool {
        // This is a simplified check
        // In a real app, you would need to check if the keyboard extension is enabled
        guard let keyboards = UserDefaults.standard.object(forKey: "AppleKeyboards") as? [String] else {
            return false
        }
        
        let bundleID = Bundle.main.bundleIdentifier ?? ""
        let keyboardID = "\(bundleID).keyboard"
        
        return keyboards.contains(keyboardID)
    }
    
    private func showEnableInstructions() {
        let alert = UIAlertController(
            title: "How to Enable Keyboard",
            message: """
            1. Tap 'Enable Keyboard' to open Settings
            2. Go to General > Keyboard > Keyboards
            3. Tap 'Add New Keyboard'
            4. Select 'QWERTY Mini Wide'
            5. Allow Full Access for all features
            """,
            preferredStyle: .alert
        )
        
        alert.addAction(UIAlertAction(title: "Got it!", style: .default))
        present(alert, animated: true)
    }
}
```

## 11. Tutorial & Help

### 11.1 TutorialViewController.swift
```swift
import UIKit

class TutorialViewController: UIViewController {
    
    private let scrollView = UIScrollView()
    private let pageControl = UIPageControl()
    private var tutorialPages: [TutorialPage] = []
    
    struct TutorialPage {
        let title: String
        let description: String
        let imageName: String
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        loadTutorialPages()
    }
    
    private func setupUI() {
        view.backgroundColor = .systemBackground
        title = "Tutorial"
        
        // Scroll view
        scrollView.isPagingEnabled = true
        scrollView.showsHorizontalScrollIndicator = false
        scrollView.delegate = self
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(scrollView)
        
        // Page control
        pageControl.currentPageIndicatorTintColor = .systemBlue
        pageControl.pageIndicatorTintColor = .systemGray3
        pageControl.addTarget(self, action: #selector(pageControlChanged), for: .valueChanged)
        pageControl.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(pageControl)
        
        NSLayoutConstraint.activate([
            scrollView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            scrollView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            scrollView.bottomAnchor.constraint(equalTo: pageControl.topAnchor, constant: -20),
            
            pageControl.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            pageControl.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -20)
        ])
    }
    
    private func loadTutorialPages() {
        tutorialPages = [
            TutorialPage(
                title: "Welcome to QWERTY Mini Wide",
                description: "A professional keyboard designed for efficient typing on iOS devices",
                imageName: "keyboard.welcome"
            ),
            TutorialPage(
                title: "Multiple Layouts",
                description: "Switch between QWERTY, numbers, symbols, and emoji keyboards with ease",
                imageName: "keyboard.layouts"
            ),
            TutorialPage(
                title: "Long Press for Special Characters",
                description: "Press and hold keys to access accented characters and symbols",
                imageName: "keyboard.longpress"
            ),
            TutorialPage(
                title: "Swipe Typing",
                description: "Glide your finger across letters to type words quickly",
                imageName: "keyboard.swipe"
            ),
            TutorialPage(
                title: "Customization",
                description: "Adjust settings like dark mode, key height, and feedback options",
                imageName: "keyboard.settings"
            ),
            TutorialPage(
                title: "Privacy First",
                description: "Your typing data stays on your device. No tracking or data collection",
                imageName: "keyboard.privacy"
            )
        ]
        
        setupPages()
        pageControl.numberOfPages = tutorialPages.count
    }
    
    private func setupPages() {
        var previousPage: UIView?
        
        for (index, tutorial) in tutorialPages.enumerated() {
            let pageView = createTutorialPageView(tutorial: tutorial)
            scrollView.addSubview(pageView)
            
            NSLayoutConstraint.activate([
                pageView.topAnchor.constraint(equalTo: scrollView.topAnchor),
                pageView.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
                pageView.widthAnchor.constraint(equalTo: scrollView.widthAnchor),
                pageView.heightAnchor.constraint(equalTo: scrollView.heightAnchor)
            ])
            
            if let previous = previousPage {
                pageView.leadingAnchor.constraint(equalTo: previous.trailingAnchor).isActive = true
            } else {
                pageView.leadingAnchor.constraint(equalTo: scrollView.leadingAnchor).isActive = true
            }
            
            if index == tutorialPages.count - 1 {
                pageView.trailingAnchor.constraint(equalTo: scrollView.trailingAnchor).isActive = true
            }
            
            previousPage = pageView
        }
    }
    
    private func createTutorialPageView(tutorial: TutorialPage) -> UIView {
        let containerView = UIView()
        containerView.translatesAutoresizingMaskIntoConstraints = false
        
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        imageView.image = UIImage(systemName: "keyboard") // Placeholder
        imageView.tintColor = .systemBlue
        imageView.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(imageView)
        
        let titleLabel = UILabel()
        titleLabel.text = tutorial.title
        titleLabel.font = .systemFont(ofSize: 24, weight: .bold)
        titleLabel.textAlignment = .center
        titleLabel.numberOfLines = 0
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(titleLabel)
        
        let descriptionLabel = UILabel()
        descriptionLabel.text = tutorial.description
        descriptionLabel.font = .systemFont(ofSize: 16)
        descriptionLabel.textAlignment = .center
        descriptionLabel.textColor = .secondaryLabel
        descriptionLabel.numberOfLines = 0
        descriptionLabel.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(descriptionLabel)
        
        NSLayoutConstraint.activate([
            imageView.centerXAnchor.constraint(equalTo: containerView.centerXAnchor),
            imageView.topAnchor.constraint(equalTo: containerView.topAnchor, constant: 80),
            imageView.widthAnchor.constraint(equalToConstant: 200),
            imageView.heightAnchor.constraint(equalToConstant: 200),
            
            titleLabel.leadingAnchor.constraint(equalTo: containerView.leadingAnchor, constant: 40),
            titleLabel.trailingAnchor.constraint(equalTo: containerView.trailingAnchor, constant: -40),
            titleLabel.topAnchor.constraint(equalTo: imageView.bottomAnchor, constant: 40),
            
            descriptionLabel.leadingAnchor.constraint(equalTo: titleLabel.leadingAnchor),
            descriptionLabel.trailingAnchor.constraint(equalTo: titleLabel.trailingAnchor),
            descriptionLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 20)
        ])
        
        return containerView
    }
    
    @objc private func pageControlChanged() {
        let page = pageControl.currentPage
        let offset = CGPoint(x: CGFloat(page) * scrollView.frame.width, y: 0)
        scrollView.setContentOffset(offset, animated: true)
    }
}

extension TutorialViewController: UIScrollViewDelegate {
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let page = Int(scrollView.contentOffset.x / scrollView.frame.width)
        pageControl.currentPage = page
    }
}
```

## 12. Accessibility Support

### 12.1 AccessibilityManager.swift
```swift
import UIKit

class AccessibilityManager {
    
    static let shared = AccessibilityManager()
    
    private init() {}
    
    // MARK: - VoiceOver Support
    func configureKeyButtonAccessibility(_ button: KeyButton, key: KeyboardKey) {
        button.isAccessibilityElement = true
        
        switch key.type {
        case .character(let char):
            button.accessibilityLabel = char.uppercased()
            button.accessibilityHint = "Double tap to type \(char)"
            button.accessibilityTraits = .keyboardKey
            
        case .space:
            button.accessibilityLabel = "Space"
            button.accessibilityHint = "Double tap to insert space"
            button.accessibilityTraits = .keyboardKey
            
        case .backspace:
            button.accessibilityLabel = "Delete"
            button.accessibilityHint = "Double tap to delete last character"
            button.accessibilityTraits = .keyboardKey
            
        case .enter:
            button.accessibilityLabel = "Return"
            button.accessibilityHint = "Double tap to insert new line"
            button.accessibilityTraits = .keyboardKey
            
        case .shift:
            let shiftState = button.isShifted ? "on" : "off"
            button.accessibilityLabel = "Shift"
            button.accessibilityValue = shiftState
            button.accessibilityHint = "Double tap to toggle shift. Double tap twice for caps lock"
            button.accessibilityTraits = [.keyboardKey, .button]
            
        case .numbers:
            button.accessibilityLabel = "Numbers"
            button.accessibilityHint = "Double tap to switch to number keyboard"
            button.accessibilityTraits = [.keyboardKey, .button]
            
        case .languageChange:
            button.accessibilityLabel = "Change Language"
            button.accessibilityHint = "Double tap to switch input language"
            button.accessibilityTraits = [.keyboardKey, .button]
            
        case .emoji:
            button.accessibilityLabel = "Emoji"
            button.accessibilityHint = "Double tap to open emoji keyboard"
            button.accessibilityTraits = [.keyboardKey, .button]
            
        default:
            button.accessibilityLabel = key.label
            button.accessibilityTraits = .keyboardKey
        }
        
        // Add long press hint if applicable
        if let longPressKeys = key.longPressKeys, !longPressKeys.isEmpty {
            button.accessibilityHint?.append(". Press and hold for more options")
        }
    }
    
    // MARK: - Announcements
    func announceKeyPress(_ character: String) {
        guard UIAccessibility.isVoiceOverRunning else { return }
        
        let announcement = character == " " ? "space" : character
        UIAccessibility.post(notification: .announcement, argument: announcement)
    }
    
    func announceLayoutChange(_ layoutName: String) {
        guard UIAccessibility.isVoiceOverRunning else { return }
        
        let announcement = "Switched to \(layoutName) keyboard"
        UIAccessibility.post(notification: .screenChanged, argument: announcement)
    }
    
    func announceShiftState(isEnabled: Bool, isCapsLock: Bool) {
        guard UIAccessibility.isVoiceOverRunning else { return }
        
        let announcement: String
        if isCapsLock {
            announcement = "Caps lock on"
        } else if isEnabled {
            announcement = "Shift on"
        } else {
            announcement = "Shift off"
        }
        
        UIAccessibility.post(notification: .announcement, argument: announcement)
    }
    
    // MARK: - Dynamic Type Support
    func scaledFont(for textStyle: UIFont.TextStyle, defaultSize: CGFloat) -> UIFont {
        if UIApplication.shared.preferredContentSizeCategory.isAccessibilityCategory {
            return UIFont.preferredFont(forTextStyle: textStyle)
        } else {
            return UIFont.systemFont(ofSize: defaultSize)
        }
    }
    
    // MARK: - Reduce Motion
    func shouldReduceMotion() -> Bool {
        return UIAccessibility.isReduceMotionEnabled
    }
    
    // MARK: - Bold Text
    func shouldUseBoldText() -> Bool {
        return UIAccessibility.isBoldTextEnabled
    }
    
    // MARK: - High Contrast
    func shouldIncreaseContrast() -> Bool {
        return UIAccessibility.isDarkerSystemColorsEnabled
    }
    
    // MARK: - Guided Access
    func isGuidedAccessEnabled() -> Bool {
        return UIAccessibility.isGuidedAccessEnabled
    }
    
    // MARK: - Switch Control
    func configureSwitchControl(for view: UIView) {
        guard UIAccessibility.isSwitchControlRunning else { return }
        
        // Configure view for switch control
        view.accessibilityTraits.insert(.allowsDirectInteraction)
    }
}

// MARK: - Accessibility Extensions
extension KeyButton {
    override var accessibilityPath: UIBezierPath? {
        get {
            return UIBezierPath(roundedRect: bounds, cornerRadius: layer.cornerRadius)
        }
        set {}
    }
    
    override func accessibilityActivate() -> Bool {
        // Handle accessibility activation
        sendActions(for: .touchUpInside)
        return true
    }
    
    override func accessibilityIncrement() {
        // Handle increment for adjustable traits
    }
    
    override func accessibilityDecrement() {
        // Handle decrement for adjustable traits
    }
}
```

## 13. Feedback & Support

### 13.1 FeedbackViewController.swift
```swift
import UIKit
import MessageUI

class FeedbackViewController: UIViewController {
    
    private let tableView = UITableView(frame: .zero, style: .insetGrouped)
    private var feedbackOptions: [FeedbackOption] = []
    
    struct FeedbackOption {
        let title: String
        let icon: String
        let action: FeedbackAction
    }
    
    enum FeedbackAction {
        case email
        case review
        case reportBug
        case featureRequest
        case faq
        case privacyPolicy
        case termsOfService
        case version
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        loadFeedbackOptions()
    }
    
    private func setupUI() {
        view.backgroundColor = .systemBackground
        title = "Feedback & Support"
        
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")
        tableView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(tableView)
        
        NSLayoutConstraint.activate([
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            tableView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            tableView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
    }
    
    private func loadFeedbackOptions() {
        feedbackOptions = [
            FeedbackOption(title: "Send Feedback", icon: "envelope", action: .email),
            FeedbackOption(title: "Rate on App Store", icon: "star", action: .review),
            FeedbackOption(title: "Report a Bug", icon: "ant", action: .reportBug),
            FeedbackOption(title: "Request a Feature", icon: "lightbulb", action: .featureRequest),
            FeedbackOption(title: "FAQ", icon: "questionmark.circle", action: .faq),
            FeedbackOption(title: "Privacy Policy", icon: "lock.shield", action: .privacyPolicy),
            FeedbackOption(title: "Terms of Service", icon: "doc.text", action: .termsOfService),
            FeedbackOption(title: "Version", icon: "info.circle", action: .version)
        ]
    }
    
    private func handleFeedbackAction(_ action: FeedbackAction) {
        switch action {
        case .email:
            sendEmail()
        case .review:
            openAppStore()
        case .reportBug:
            reportBug()
        case .featureRequest:
            requestFeature()
        case .faq:
            showFAQ()
        case .privacyPolicy:
            showPrivacyPolicy()
        case .termsOfService:
            showTermsOfService()
        case .version:
            showVersionInfo()
        }
    }
    
    private func sendEmail() {
        guard MFMailComposeViewController.canSendMail() else {
            showAlert(title: "Email Not Available", message: "Please configure your email account in Settings.")
            return
        }
        
        let mailComposer = MFMailComposeViewController()
        mailComposer.mailComposeDelegate = self
        mailComposer.setToRecipients(["support@qwertyminiwide.com"])
        mailComposer.setSubject("QWERTY Mini Wide Feedback")
        
        let deviceInfo = """
        
        ---
        Device: \(UIDevice.current.model)
        iOS Version: \(UIDevice.current.systemVersion)
        App Version: \(Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "Unknown")
        """
        
        mailComposer.setMessageBody("Please write your feedback here:\n\n\n\(deviceInfo)", isHTML: false)
        present(mailComposer, animated: true)
    }
    
    private func openAppStore() {
        let appStoreURL = "https://apps.apple.com/app/idYOURAPPID"
        if let url = URL(string: appStoreURL) {
            UIApplication.shared.open(url)
        }
    }
    
    private func reportBug() {
        let bugReportVC = BugReportViewController()
        navigationController?.pushViewController(bugReportVC, animated: true)
    }
    
    private func requestFeature() {
        let featureRequestVC = FeatureRequestViewController()
        navigationController?.pushViewController(featureRequestVC, animated: true)
    }
    
    private func showFAQ() {
        let faqVC = FAQViewController()
        navigationController?.pushViewController(faqVC, animated: true)
    }
    
    private func showPrivacyPolicy() {
        if let url = URL(string: "https://qwertyminiwide.com/privacy") {
            UIApplication.shared.open(url)
        }
    }
    
    private func showTermsOfService() {
        if let url = URL(string: "https://qwertyminiwide.com/terms") {
            UIApplication.shared.open(url)
        }
    }
    
    private func showVersionInfo() {
        let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "Unknown"
        let build = Bundle.main.infoDictionary?["CFBundleVersion"] as? String ?? "Unknown"
        
        showAlert(
            title: "Version Information",
            message: "Version: \(version)\nBuild: \(build)\n\nThank you for using QWERTY Mini Wide!"
        )
    }
    
    private func showAlert(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
}

// MARK: - UITableViewDataSource
extension FeedbackViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return feedbackOptions.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
        let option = feedbackOptions[indexPath.row]
        
        cell.textLabel?.text = option.title
        cell.imageView?.image = UIImage(systemName: option.icon)
        cell.accessoryType = option.action == .version ? .none : .disclosureIndicator
        
        return cell
    }
}

// MARK: - UITableViewDelegate
extension FeedbackViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let option = feedbackOptions[indexPath.row]
        handleFeedbackAction(option.action)
    }
}

// MARK: - MFMailComposeViewControllerDelegate
extension FeedbackViewController: MFMailComposeViewControllerDelegate {
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        controller.dismiss(animated: true)
    }
}

// MARK: - Supporting View Controllers
class BugReportViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemBackground
        title = "Report a Bug"
        // Implementation for bug reporting
    }
}

class FeatureRequestViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemBackground
        title = "Request a Feature"
        // Implementation for feature requests
    }
}

class FAQViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemBackground
        title = "Frequently Asked Questions"
        // Implementation for FAQ
    }
}
```

## 14. Build Configuration

### 14.1 Podfile
```ruby
platform :ios, '14.0'
use_frameworks!

target 'QWERTYMiniWide' do
  # Analytics (optional)
  # pod 'Firebase/Analytics'
  
  # Crash reporting (optional)
  # pod 'Firebase/Crashlytics'
  
  # Testing
  pod 'Quick', '~> 5.0', :configurations => ['Debug']
  pod 'Nimble', '~> 10.0', :configurations => ['Debug']
end

target 'QWERTYKeyboard' do
  # Keyboard extension dependencies
end

post_install do |installer|
  installer.pods_project.targets.each do |target|
    target.build_configurations.each do |config|
      config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '14.0'
    }
  end
end
```

### 14.2 App Extensions Configuration

In Xcode, add a new target:
1. File > New > Target
2. Select "Custom Keyboard Extension"
3. Name: "QWERTYKeyboard"
4. Embed in Application: QWERTYMiniWide

### 14.3 Entitlements

App.entitlements:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>com.apple.security.application-groups</key>
    <array>
        <string>group.com.qwertyminiwide.keyboard</string>
    </array>
</dict>
</plist>
```

KeyboardExtension.entitlements:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>com.apple.security.application-groups</key>
    <array>
        <string>group.com.qwertyminiwide.keyboard</string>
    </array>
    <key>RequestsOpenAccess</key>
    <true/>
</dict>
</plist>
```

## Summary

This comprehensive iOS implementation of the QWERTY Mini Wide keyboard includes:

1. **Core Architecture**: Complete keyboard extension with modular design
2. **Multiple Layouts**: QWERTY, numbers, symbols, and emoji support
3. **Advanced Features**: Auto-correction, predictive text, swipe typing
4. **Customization**: Dark mode, haptic feedback, sound settings
5. **Accessibility**: Full VoiceOver support, dynamic type, guided access
6. **User Experience**: Tutorial, settings, feedback system
7. **Performance**: Optimized rendering, efficient memory management
8. **Privacy**: No data collection, offline functionality
9. **Localization**: Multi-language support with special characters
10. **Modern iOS**: Support for iOS 14+, SwiftUI ready, dark mode

The implementation follows iOS best practices, Apple's Human Interface Guidelines, and provides a professional keyboard experience comparable to the Android version.
```