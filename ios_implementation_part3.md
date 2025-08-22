# iOS QWERTY Mini Wide Keyboard Implementation - Part 3: Advanced Features & Extensions

## 7. Text Processing & Auto-Correction

### 7.1 TextProcessor.swift
```swift
import Foundation
import UIKit

class TextProcessor {
    
    // MARK: - Properties
    static let shared = TextProcessor()
    private var wordDictionary: Set<String> = []
    private var userDictionary: Set<String> = []
    private var autocorrectionMap: [String: String] = [:]
    private var wordFrequency: [String: Int] = [:]
    private var contextBuffer: String = ""
    private let maxContextLength = 100
    
    // Auto-correction settings
    private var isAutoCorrectionEnabled = true
    private var isAutoCapitalizationEnabled = true
    private var isPredictiveTextEnabled = true
    
    // MARK: - Initialization
    private init() {
        loadDictionaries()
        loadUserPreferences()
    }
    
    private func loadDictionaries() {
        // Load system dictionary
        if let path = Bundle.main.path(forResource: "words", ofType: "txt"),
           let content = try? String(contentsOfFile: path) {
            wordDictionary = Set(content.components(separatedBy: .newlines))
        }
        
        // Load user dictionary from UserDefaults
        if let userWords = UserDefaults.standard.array(forKey: "user_dictionary") as? [String] {
            userDictionary = Set(userWords)
        }
        
        // Load common autocorrections
        loadAutocorrectionMap()
        
        // Load word frequency data
        loadWordFrequency()
    }
    
    private func loadAutocorrectionMap() {
        autocorrectionMap = [
            "teh": "the",
            "adn": "and",
            "taht": "that",
            "thsi": "this",
            "wtih": "with",
            "ahve": "have",
            "yuo": "you",
            "yuor": "your",
            "waht": "what",
            "wnat": "want",
            "cna": "can",
            "dont": "don't",
            "wont": "won't",
            "cant": "can't",
            "didnt": "didn't",
            "doesnt": "doesn't",
            "wouldnt": "wouldn't",
            "shouldnt": "shouldn't",
            "couldnt": "couldn't",
            "thats": "that's",
            "whats": "what's",
            "heres": "here's",
            "theres": "there's",
            "wheres": "where's",
            "whos": "who's",
            "its": "it's",
            "lets": "let's",
            "youre": "you're",
            "theyre": "they're",
            "were": "we're",
            "ive": "I've",
            "im": "I'm",
            "id": "I'd",
            "ill": "I'll"
        ]
    }
    
    private func loadWordFrequency() {
        // Load most common English words with their frequency
        wordFrequency = [
            "the": 10000,
            "be": 9500,
            "to": 9000,
            "of": 8500,
            "and": 8000,
            "a": 7500,
            "in": 7000,
            "that": 6500,
            "have": 6000,
            "I": 5500,
            "it": 5000,
            "for": 4500,
            "not": 4000,
            "on": 3500,
            "with": 3000,
            "he": 2500,
            "as": 2000,
            "you": 1500,
            "do": 1000,
            "at": 500
        ]
    }
    
    private func loadUserPreferences() {
        let defaults = UserDefaults.standard
        isAutoCorrectionEnabled = defaults.bool(forKey: "auto_correction")
        isAutoCapitalizationEnabled = defaults.bool(forKey: "auto_capitalization")
        isPredictiveTextEnabled = defaults.bool(forKey: "predictive_text")
    }
    
    // MARK: - Auto-Correction
    func shouldAutoCorrect(_ word: String) -> String? {
        guard isAutoCorrectionEnabled else { return nil }
        
        let lowercased = word.lowercased()
        
        // Check direct autocorrection map
        if let correction = autocorrectionMap[lowercased] {
            return matchCase(original: word, corrected: correction)
        }
        
        // Check if word exists in dictionary
        if wordDictionary.contains(lowercased) || userDictionary.contains(lowercased) {
            return nil
        }
        
        // Try to find the best correction
        if let bestMatch = findBestCorrection(for: lowercased) {
            return matchCase(original: word, corrected: bestMatch)
        }
        
        return nil
    }
    
    private func findBestCorrection(for word: String) -> String? {
        var bestMatch: String?
        var bestScore = Int.max
        
        // Check words with edit distance of 1
        let candidates = generateCandidates(for: word)
        
        for candidate in candidates {
            if wordDictionary.contains(candidate) || userDictionary.contains(candidate) {
                let score = editDistance(word, candidate)
                if score < bestScore {
                    bestScore = score
                    bestMatch = candidate
                }
            }
        }
        
        return bestMatch
    }
    
    private func generateCandidates(for word: String) -> Set<String> {
        var candidates = Set<String>()
        let letters = "abcdefghijklmnopqrstuvwxyz"
        
        // Deletions
        for i in word.indices {
            var newWord = word
            newWord.remove(at: i)
            candidates.insert(newWord)
        }
        
        // Transpositions
        var chars = Array(word)
        for i in 0..<chars.count-1 {
            chars.swapAt(i, i+1)
            candidates.insert(String(chars))
            chars.swapAt(i, i+1) // Swap back
        }
        
        // Replacements
        for i in word.indices {
            for letter in letters {
                var newWord = word
                newWord.replaceSubrange(i...i, with: String(letter))
                candidates.insert(newWord)
            }
        }
        
        // Insertions
        for i in word.indices {
            for letter in letters {
                var newWord = word
                newWord.insert(letter, at: i)
                candidates.insert(newWord)
            }
        }
        
        // Insertion at the end
        for letter in letters {
            candidates.insert(word + String(letter))
        }
        
        return candidates
    }
    
    private func editDistance(_ s1: String, _ s2: String) -> Int {
        let m = s1.count
        let n = s2.count
        
        if m == 0 { return n }
        if n == 0 { return m }
        
        var matrix = Array(repeating: Array(repeating: 0, count: n + 1), count: m + 1)
        
        for i in 0...m {
            matrix[i][0] = i
        }
        
        for j in 0...n {
            matrix[0][j] = j
        }
        
        let s1Array = Array(s1)
        let s2Array = Array(s2)
        
        for i in 1...m {
            for j in 1...n {
                if s1Array[i-1] == s2Array[j-1] {
                    matrix[i][j] = matrix[i-1][j-1]
                } else {
                    matrix[i][j] = min(
                        matrix[i-1][j] + 1,    // deletion
                        matrix[i][j-1] + 1,    // insertion
                        matrix[i-1][j-1] + 1   // substitution
                    )
                }
            }
        }
        
        return matrix[m][n]
    }
    
    private func matchCase(original: String, corrected: String) -> String {
        if original == original.uppercased() {
            return corrected.uppercased()
        } else if original.first?.isUppercase == true {
            return corrected.prefix(1).uppercased() + corrected.dropFirst()
        }
        return corrected
    }
    
    // MARK: - Auto-Capitalization
    func shouldCapitalize(context: String) -> Bool {
        guard isAutoCapitalizationEnabled else { return false }
        
        let trimmed = context.trimmingCharacters(in: .whitespacesAndNewlines)
        
        // Beginning of text
        if trimmed.isEmpty {
            return true
        }
        
        // After sentence-ending punctuation
        let sentenceEnders = [".", "!", "?"]
        for ender in sentenceEnders {
            if trimmed.hasSuffix(ender) || trimmed.hasSuffix(ender + " ") {
                return true
            }
        }
        
        return false
    }
    
    // MARK: - Word Prediction
    func getPredictions(for prefix: String, context: String) -> [String] {
        guard isPredictiveTextEnabled else { return [] }
        
        let lowercasedPrefix = prefix.lowercased()
        var predictions: [(word: String, score: Int)] = []
        
        // Search in dictionaries
        let allWords = wordDictionary.union(userDictionary)
        
        for word in allWords {
            if word.hasPrefix(lowercasedPrefix) && word != lowercasedPrefix {
                let score = calculatePredictionScore(word: word, context: context)
                predictions.append((word, score))
            }
        }
        
        // Sort by score and take top 3
        predictions.sort { $0.score > $1.score }
        return Array(predictions.prefix(3).map { $0.word })
    }
    
    private func calculatePredictionScore(word: String, context: String) -> Int {
        var score = 0
        
        // Word frequency score
        if let frequency = wordFrequency[word] {
            score += frequency
        }
        
        // Context relevance score
        if context.contains(word) {
            score += 1000
        }
        
        // Length penalty (prefer shorter words)
        score -= word.count * 10
        
        return score
    }
    
    // MARK: - User Dictionary Management
    func addToUserDictionary(_ word: String) {
        userDictionary.insert(word.lowercased())
        saveUserDictionary()
    }
    
    func removeFromUserDictionary(_ word: String) {
        userDictionary.remove(word.lowercased())
        saveUserDictionary()
    }
    
    private func saveUserDictionary() {
        UserDefaults.standard.set(Array(userDictionary), forKey: "user_dictionary")
    }
    
    // MARK: - Context Management
    func updateContext(_ text: String) {
        contextBuffer += text
        if contextBuffer.count > maxContextLength {
            contextBuffer = String(contextBuffer.suffix(maxContextLength))
        }
    }
    
    func clearContext() {
        contextBuffer = ""
    }
    
    func getCurrentContext() -> String {
        return contextBuffer
    }
}

// MARK: - Predictive Text Bar
class PredictiveTextBar: UIView {
    
    // MARK: - Properties
    weak var delegate: PredictiveTextBarDelegate?
    private var stackView: UIStackView!
    private var suggestionButtons: [UIButton] = []
    private var isDarkMode = false
    
    // MARK: - Initialization
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupView()
    }
    
    private func setupView() {
        backgroundColor = UIColor.systemBackground
        layer.borderWidth = 0.5
        layer.borderColor = UIColor.separator.cgColor
        
        // Stack view for suggestions
        stackView = UIStackView()
        stackView.axis = .horizontal
        stackView.distribution = .fillEqually
        stackView.spacing = 1
        stackView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(stackView)
        
        NSLayoutConstraint.activate([
            stackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 8),
            stackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -8),
            stackView.topAnchor.constraint(equalTo: topAnchor),
            stackView.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
        
        // Create suggestion buttons
        for _ in 0..<3 {
            let button = UIButton(type: .system)
            button.titleLabel?.font = .systemFont(ofSize: 16)
            button.addTarget(self, action: #selector(suggestionTapped(_:)), for: .touchUpInside)
            button.layer.cornerRadius = 4
            button.layer.borderWidth = 0.5
            suggestionButtons.append(button)
            stackView.addArrangedSubview(button)
        }
        
        updateAppearance()
    }
    
    // MARK: - Public Methods
    func setSuggestions(_ suggestions: [String]) {
        for (index, button) in suggestionButtons.enumerated() {
            if index < suggestions.count {
                button.setTitle(suggestions[index], for: .normal)
                button.isHidden = false
            } else {
                button.isHidden = true
            }
        }
    }
    
    func clearSuggestions() {
        suggestionButtons.forEach { $0.isHidden = true }
    }
    
    func updateAppearance(isDarkMode: Bool = false) {
        self.isDarkMode = isDarkMode
        
        backgroundColor = isDarkMode ? UIColor(white: 0.15, alpha: 1.0) : UIColor(white: 0.97, alpha: 1.0)
        layer.borderColor = isDarkMode ? UIColor(white: 0.3, alpha: 1.0).cgColor : UIColor.separator.cgColor
        
        for button in suggestionButtons {
            button.layer.borderColor = isDarkMode ? UIColor(white: 0.3, alpha: 1.0).cgColor : UIColor.separator.cgColor
            button.setTitleColor(isDarkMode ? .white : .black, for: .normal)
            button.backgroundColor = isDarkMode ? UIColor(white: 0.2, alpha: 1.0) : UIColor.white
        }
    }
    
    // MARK: - Actions
    @objc private func suggestionTapped(_ sender: UIButton) {
        guard let suggestion = sender.title(for: .normal) else { return }
        delegate?.predictiveTextBar(self, didSelectSuggestion: suggestion)
        HapticManager.shared.triggerSelection()
    }
}

protocol PredictiveTextBarDelegate: AnyObject {
    func predictiveTextBar(_ bar: PredictiveTextBar, didSelectSuggestion suggestion: String)
}
```

## 8. Gesture Recognition & Swipe Typing

### 8.1 SwipeTypingEngine.swift
```swift
import UIKit
import CoreGraphics

class SwipeTypingEngine {
    
    // MARK: - Properties
    static let shared = SwipeTypingEngine()
    private var isEnabled = false
    private var currentPath: [CGPoint] = []
    private var keyLocations: [String: CGRect] = [:]
    private var startTime: TimeInterval = 0
    private var possibleWords: [String] = []
    
    // Swipe path analysis
    private struct SwipePattern {
        let path: [CGPoint]
        let velocity: CGFloat
        let duration: TimeInterval
        let keySequence: [String]
    }
    
    // MARK: - Initialization
    private init() {
        loadSettings()
    }
    
    private func loadSettings() {
        isEnabled = UserDefaults.standard.bool(forKey: "swipe_typing_enabled")
    }
    
    // MARK: - Path Recording
    func startSwipe(at point: CGPoint) {
        guard isEnabled else { return }
        
        currentPath = [point]
        startTime = Date().timeIntervalSince1970
        possibleWords.removeAll()
    }
    
    func updateSwipe(to point: CGPoint) {
        guard isEnabled else { return }
        
        // Add point to path if it's far enough from the last point
        if let lastPoint = currentPath.last {
            let distance = sqrt(pow(point.x - lastPoint.x, 2) + pow(point.y - lastPoint.y, 2))
            if distance > 5 { // Minimum distance threshold
                currentPath.append(point)
            }
        }
    }
    
    func endSwipe(at point: CGPoint) -> String? {
        guard isEnabled else { return nil }
        
        currentPath.append(point)
        let duration = Date().timeIntervalSince1970 - startTime
        
        // Analyze the swipe path
        let pattern = analyzeSwipePath(duration: duration)
        
        // Find matching words
        if let bestWord = findBestWord(for: pattern) {
            currentPath.removeAll()
            return bestWord
        }
        
        currentPath.removeAll()
        return nil
    }
    
    func cancelSwipe() {
        currentPath.removeAll()
        possibleWords.removeAll()
    }
    
    // MARK: - Path Analysis
    private func analyzeSwipePath(duration: TimeInterval) -> SwipePattern {
        let keySequence = extractKeySequence(from: currentPath)
        let velocity = calculateVelocity(path: currentPath, duration: duration)
        
        return SwipePattern(
            path: currentPath,
            velocity: velocity,
            duration: duration,
            keySequence: keySequence
        )
    }
    
    private func extractKeySequence(from path: [CGPoint]) -> [String] {
        var sequence: [String] = []
        var lastKey: String?
        
        for point in path {
            if let key = findKey(at: point) {
                if key != lastKey {
                    sequence.append(key)
                    lastKey = key
                }
            }
        }
        
        return sequence
    }
    
    private func findKey(at point: CGPoint) -> String? {
        for (key, frame) in keyLocations {
            if frame.contains(point) {
                return key
            }
        }
        return nil
    }
    
    private func calculateVelocity(path: [CGPoint], duration: TimeInterval) -> CGFloat {
        guard path.count > 1, duration > 0 else { return 0 }
        
        var totalDistance: CGFloat = 0
        for i in 1..<path.count {
            let distance = sqrt(pow(path[i].x - path[i-1].x, 2) + pow(path[i].y - path[i-1].y, 2))
            totalDistance += distance
        }
        
        return totalDistance / CGFloat(duration)
    }
    
    // MARK: - Word Matching
    private func findBestWord(for pattern: SwipePattern) -> String? {
        let candidates = generateCandidateWords(from: pattern.keySequence)
        
        if candidates.isEmpty {
            return nil
        }
        
        // Score each candidate
        var scoredCandidates: [(word: String, score: Double)] = []
        
        for candidate in candidates {
            let score = calculateWordScore(
                word: candidate,
                pattern: pattern
            )
            scoredCandidates.append((candidate, score))
        }
        
        // Sort by score and return the best match
        scoredCandidates.sort { $0.score > $1.score }
        return scoredCandidates.first?.word
    }
    
    private func generateCandidateWords(from keySequence: [String]) -> [String] {
        // This is a simplified implementation
        // In a real app, you would use a more sophisticated algorithm
        // possibly with a trie data structure for efficient lookup
        
        var candidates: [String] = []
        let sequenceString = keySequence.joined()
        
        // Check dictionary for words that could match this key sequence
        let dictionary = TextProcessor.shared
        
        // For demonstration, return some common words
        // In reality, this would search through a proper word list
        if sequenceString.contains("hello") {
            candidates.append("hello")
        }
        if sequenceString.contains("world") {
            candidates.append("world")
        }
        
        return candidates
    }
    
    private func calculateWordScore(word: String, pattern: SwipePattern) -> Double {
        var score = 0.0
        
        // Length similarity
        let lengthDiff = abs(word.count - pattern.keySequence.count)
        score += 100.0 / (1.0 + Double(lengthDiff))
        
        // Pattern match score
        let patternScore = calculatePatternMatchScore(word: word, keySequence: pattern.keySequence)
        score += patternScore * 50
        
        // Word frequency score
        // Higher frequency words get higher scores
        // This would use actual frequency data in a real implementation
        
        return score
    }
    
    private func calculatePatternMatchScore(word: String, keySequence: [String]) -> Double {
        // Calculate how well the word matches the key sequence
        var matchCount = 0
        let wordChars = Array(word)
        
        for (index, char) in wordChars.enumerated() {
            if index < keySequence.count {
                if keySequence[index] == String(char) {
                    matchCount += 1
                }
            }
        }
        
        return Double(matchCount) / Double(max(word.count, keySequence.count))
    }
    
    // MARK: - Key Location Management
    func updateKeyLocations(_ locations: [String: CGRect]) {
        keyLocations = locations
    }
    
    func setEnabled(_ enabled: Bool) {
        isEnabled = enabled
        UserDefaults.standard.set(enabled, forKey: "swipe_typing_enabled")
    }
}

// MARK: - Gesture Handler
class SwipeGestureHandler: NSObject {
    
    weak var keyboardView: KeyboardView?
    private var swipeEngine = SwipeTypingEngine.shared
    private var isSwipeInProgress = false
    private var swipePathLayer: CAShapeLayer?
    
    func handleTouchBegan(_ touch: UITouch, in view: UIView) {
        let location = touch.location(in: view)
        swipeEngine.startSwipe(at: location)
        isSwipeInProgress = true
        
        // Start drawing swipe path
        drawSwipePath(startingAt: location, in: view)
    }
    
    func handleTouchMoved(_ touch: UITouch, in view: UIView) {
        guard isSwipeInProgress else { return }
        
        let location = touch.location(in: view)
        swipeEngine.updateSwipe(to: location)
        
        // Update swipe path visualization
        updateSwipePath(to: location)
    }
    
    func handleTouchEnded(_ touch: UITouch, in view: UIView) -> String? {
        guard isSwipeInProgress else { return nil }
        
        let location = touch.location(in: view)
        let result = swipeEngine.endSwipe(at: location)
        
        // Clear swipe path visualization
        clearSwipePath()
        isSwipeInProgress = false
        
        return result
    }
    
    func handleTouchCancelled() {
        swipeEngine.cancelSwipe()
        clearSwipePath()
        isSwipeInProgress = false
    }
    
    // MARK: - Path Visualization
    private func drawSwipePath(startingAt point: CGPoint, in view: UIView) {
        swipePathLayer?.removeFromSuperlayer()
        
        swipePathLayer = CAShapeLayer()
        swipePathLayer?.strokeColor = UIColor.systemBlue.withAlphaComponent(0.6).cgColor
        swipePathLayer?.fillColor = UIColor.clear.cgColor
        swipePathLayer?.lineWidth = 3.0
        swipePathLayer?.lineCap = .round
        swipePathLayer?.lineJoin = .round
        
        let path = UIBezierPath()
        path.move(to: point)
        swipePathLayer?.path = path.cgPath
        
        view.layer.addSublayer(swipePathLayer!)
    }
    
    private func updateSwipePath(to point: CGPoint) {
        guard let layer = swipePathLayer else { return }
        
        let path = UIBezierPath(cgPath: layer.path ?? CGPath(rect: .zero, transform: nil))
        path.addLine(to: point)
        
        CATransaction.begin()
        CATransaction.setDisableActions(true)
        layer.path = path.cgPath
        CATransaction.commit()
    }
    
    private func clearSwipePath() {
        swipePathLayer?.removeFromSuperlayer()
        swipePathLayer = nil
    }
}
```

## 9. Emoji Keyboard Implementation

### 9.1 EmojiKeyboard.swift
```swift
import UIKit

class EmojiKeyboardViewController: UIViewController {
    
    // MARK: - Properties
    private let collectionView: UICollectionView
    private let categoryBar: EmojiCategoryBar
    private var emojiData: [EmojiCategory] = []
    private var recentEmojis: [String] = []
    private var searchController: UISearchController?
    weak var delegate: EmojiKeyboardDelegate?
    
    struct EmojiCategory {
        let name: String
        let icon: String
        let emojis: [String]
    }
    
    // MARK: - Initialization
    init() {
        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .vertical
        layout.minimumInteritemSpacing = 8
        layout.minimumLineSpacing = 8
        layout.sectionInset = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10)
        
        collectionView = UICollectionView(frame: .zero, collectionViewLayout: layout)
        categoryBar = EmojiCategoryBar()
        
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - View Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        loadEmojiData()
        loadRecentEmojis()
    }
    
    private func setupUI() {
        view.backgroundColor = .systemBackground
        
        // Category bar
        categoryBar.delegate = self
        categoryBar.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(categoryBar)
        
        // Collection view
        collectionView.backgroundColor = .clear
        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.register(EmojiCell.self, forCellWithReuseIdentifier: "EmojiCell")
        collectionView.register(
            EmojiSectionHeader.self,
            forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader,
            withReuseIdentifier: "Header"
        )
        collectionView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(collectionView)
        
        // Constraints
        NSLayoutConstraint.activate([
            categoryBar.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            categoryBar.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            categoryBar.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            categoryBar.heightAnchor.constraint(equalToConstant: 44),
            
            collectionView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            collectionView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            collectionView.topAnchor.constraint(equalTo: categoryBar.bottomAnchor),
            collectionView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
    }
    
    private func loadEmojiData() {
        emojiData = [
            EmojiCategory(
                name: "Recent",
                icon: "🕐",
                emojis: recentEmojis
            ),
            EmojiCategory(
                name: "Smileys & People",
                icon: "😀",
                emojis: ["😀", "😃", "😄", "😁", "😅", "😂", "🤣", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🤫", "🤔", "🤐", "🤨", "😐", "😑", "😶", "😏", "😒", "🙄", "😬", "🤥", "😌", "😔", "😪", "🤤", "😴", "😷", "🤒", "🤕", "🤢", "🤮", "🤧", "🥵", "🥶", "🥴", "😵", "🤯", "🤠", "🥳", "😎", "🤓", "🧐", "😕", "😟", "🙁", "☹️", "😮", "😯", "😲", "😳", "🥺", "😦", "😧", "😨", "😰", "😥", "😢", "😭", "😱", "😖", "😣", "😞", "😓", "😩", "😫", "🥱", "😤", "😡", "😠", "🤬", "😈", "👿", "💀", "☠️", "💩", "🤡", "👹", "👺", "👻", "👽", "👾", "🤖"]
            ),
            EmojiCategory(
                name: "Animals & Nature",
                icon: "🐶",
                emojis: ["🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐽", "🐸", "🐵", "🙈", "🙉", "🙊", "🐒", "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉", "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞", "🐜", "🦟", "🦗", "🕷", "🕸", "🦂", "🐢", "🐍", "🦎", "🦖", "🦕", "🐙", "🦑", "🦐", "🦞", "🦀", "🐡", "🐠", "🐟", "🐬", "🐳", "🐋", "🦈", "🐊", "🐅", "🐆", "🦓", "🦍", "🦧", "🐘", "🦛", "🦏", "🐪", "🐫", "🦒", "🦘", "🐃", "🐂", "🐄", "🐎", "🐖", "🐏", "🐑", "🦙", "🐐", "🦌", "🐕", "🐩", "🦮", "🐕‍🦺", "🐈", "🐓", "🦃", "🦚", "🦜", "🦢", "🦩", "🕊", "🐇", "🦝", "🦨", "🦡", "🦦", "🦥", "🐁", "🐀", "🐿", "🦔"]
            ),
            EmojiCategory(
                name: "Food & Drink",
                icon: "🍔",
                emojis: ["🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🍈", "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦", "🥬", "🥒", "🌶", "🌽", "🥕", "🧄", "🧅", "🥔", "🍠", "🥐", "🥯", "🍞", "🥖", "🥨", "🧀", "🥚", "🍳", "🧈", "🥞", "🧇", "🥓", "🥩", "🍗", "🍖", "🌭", "🍔", "🍟", "🍕", "🥪", "🥙", "🧆", "🌮", "🌯", "🥗", "🥘", "🥫", "🍝", "🍜", "🍲", "🍛", "🍣", "🍱", "🥟", "🦪", "🍤", "🍙", "🍚", "🍘", "🍥", "🥠", "🥮", "🍢", "🍡", "🍧", "🍨", "🍦", "🥧", "🧁", "🍰", "🎂", "🍮", "🍭", "🍬", "🍫", "🍿", "🍩", "🍪", "🌰", "🥜", "🍯", "🥛", "🍼", "☕", "🍵", "🧃", "🥤", "🍶", "🍺", "🍻", "🥂", "🍷", "🥃", "🍸", "🍹", "🧉", "🍾", "🧊"]
            ),
            EmojiCategory(
                name: "Activities",
                icon: "⚽",
                emojis: ["⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉", "🥏", "🎱", "🪀", "🏓", "🏸", "🏒", "🏑", "🥍", "🏏", "🥅", "⛳", "🪁", "🏹", "🎣", "🤿", "🥊", "🥋", "🎽", "🛹", "🛷", "⛸", "🥌", "🎿", "⛷", "🏂", "🪂", "🏋️", "🏋️‍♂️", "🏋️‍♀️", "🤼", "🤼‍♂️", "🤼‍♀️", "🤸", "🤸‍♂️", "🤸‍♀️", "⛹️", "⛹️‍♂️", "⛹️‍♀️", "🤺", "🤾", "🤾‍♂️", "🤾‍♀️", "🏌️", "🏌️‍♂️", "🏌️‍♀️", "🏇", "🧘", "🧘‍♂️", "🧘‍♀️", "🏄", "🏄‍♂️", "🏄‍♀️", "🏊", "🏊‍♂️", "🏊‍♀️", "🤽", "🤽‍♂️", "🤽‍♀️", "🚣", "🚣‍♂️", "🚣‍♀️", "🧗", "🧗‍♂️", "🧗‍♀️", "🚵", "🚵‍♂️", "🚵‍♀️", "🚴", "🚴‍♂️", "🚴‍♀️", "🏆", "🥇", "🥈", "🥉", "🏅", "🎖", "🏵", "🎗", "🎫", "🎟", "🎪", "🤹", "🤹‍♂️", "🤹‍♀️", "🎭", "🩰", "🎨", "🎬", "🎤", "🎧", "🎼", "🎹", "🥁", "🎷", "🎺", "🎸", "🪕", "🎻", "🎲", "♟", "🎯", "🎳", "🎮", "🎰", "🧩"]
            ),
            EmojiCategory(
                name: "Travel & Places",
                icon: "🚗",
                emojis: ["🚗", "🚕", "🚙", "🚌", "🚎", "🏎", "🚓", "🚑", "🚒", "🚐", "🚚", "🚛", "🚜", "🦯", "🦽", "🦼", "🛴", "🚲", "🛵", "🏍", "🛺", "🚨", "🚔", "🚍", "🚘", "🚖", "🚡", "🚠", "🚟", "🚃", "🚋", "🚞", "🚝", "🚄", "🚅", "🚈", "🚂", "🚆", "🚇", "🚊", "🚉", "✈️", "🛫", "🛬", "🛩", "💺", "🛰", "🚀", "🛸", "🚁", "🛶", "⛵", "🚤", "🛥", "🛳", "⛴", "🚢", "⚓", "⛽", "🚧", "🚦", "🚥", "🚏", "🗺", "🗿", "🗽", "🗼", "🏰", "🏯", "🏟", "🎡", "🎢", "🎠", "⛲", "⛱", "🏖", "🏝", "🏜", "🌋", "⛰", "🏔", "🗻", "🏕", "⛺", "🏠", "🏡", "🏘", "🏚", "🏗", "🏭", "🏢", "🏬", "🏣", "🏤", "🏥", "🏦", "🏨", "🏪", "🏫", "🏩", "💒", "🏛", "⛪", "🕌", "🕍", "🛕", "🕋", "⛩", "🛤", "🛣", "🗾", "🎑", "🏞", "🌅", "🌄", "🌠", "🎇", "🎆", "🌇", "🌆", "🏙", "🌃", "🌌", "🌉", "🌁"]
            ),
            EmojiCategory(
                name: "Objects",
                icon: "💡",
                emojis: ["⌚", "📱", "📲", "💻", "⌨️", "🖥", "🖨", "🖱", "🖲", "🕹", "🗜", "💽", "💾", "💿", "📀", "📼", "📷", "📸", "📹", "🎥", "📽", "🎞", "📞", "☎️", "📟", "📠", "📺", "📻", "🎙", "🎚", "🎛", "🧭", "⏱", "⏲", "⏰", "🕰", "⌛", "⏳", "📡", "🔋", "🔌", "💡", "🔦", "🕯", "🪔", "🧯", "🛢", "💸", "💵", "💴", "💶", "💷", "💰", "💳", "💎", "⚖️", "🧰", "🔧", "🔨", "⚒", "🛠", "⛏", "🔩", "⚙️", "🧱", "⛓", "🧲", "🔫", "💣", "🧨", "🪓", "🔪", "🗡", "⚔️", "🛡", "🚬", "⚰️", "⚱️", "🏺", "🔮", "📿", "🧿", "💈", "⚗️", "🔭", "🔬", "🕳", "🩹", "🩺", "💊", "💉", "🩸", "🧬", "🦠", "🧫", "🧪", "🌡", "🧹", "🧺", "🧻", "🚽", "🚰", "🚿", "🛁", "🛀", "🧼", "🪒", "🧽", "🧴", "🛎", "🔑", "🗝", "🚪", "🪑", "🛋", "🛏", "🛌", "🧸", "🖼", "🛍", "🛒", "🎁", "🎈", "🎏", "🎀", "🎊", "🎉", "🎎", "🏮", "🎐", "🧧", "✉️", "📩", "📨", "📧", "💌", "📥", "📤", "📦", "🏷", "📪", "📫", "📬", "📭", "📮", "📯", "📜", "📃", "📄", "📑", "🧾", "📊", "📈", "📉", "🗒", "🗓", "📆", "📅", "🗑", "📇", "🗃", "🗳", "🗄", "📋", "📁", "📂", "🗂", "🗞", "📰", "📓", "📔", "📒", "📕", "📗", "📘", "📙", "📚", "📖", "🔖", "🧷", "🔗", "📎", "🖇", "📐", "📏", "🧮", "📌", "📍", "✂️", "🖊", "🖋", "✒️", "🖌", "🖍", "📝", "✏️", "🔍", "🔎", "🔏", "🔐", "🔒", "🔓"]
            ),
            EmojiCategory(
                name: "Symbols",
                icon: "❤️",
                emojis: ["❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️", "✝️", "☪️", "🕉", "☸️", "✡️", "🔯", "🕎", "☯️", "☦️", "🛐", "⛎", "♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓", "🆔", "⚛️", "🉑", "☢️", "☣️", "📴", "📳", "🈶", "🈚", "🈸", "🈺", "🈷️", "✴️", "🆚", "💮", "🉐", "㊙️", "㊗️", "🈴", "🈵", "🈹", "🈲", "🅰️", "🅱️", "🆎", "🆑", "🅾️", "🆘", "❌", "⭕", "🛑", "⛔", "📛", "🚫", "💯", "💢", "♨️", "🚷", "🚯", "🚳", "🚱", "🔞", "📵", "🚭", "❗", "❕", "❓", "❔", "‼️", "⁉️", "🔅", "🔆", "〽️", "⚠️", "🚸", "🔱", "⚜️", "🔰", "♻️", "✅", "🈯", "💹", "❇️", "✳️", "❎", "🌐", "💠", "Ⓜ️", "🌀", "💤", "🏧", "🚾", "♿", "🅿️", "🈳", "🈂️", "🛂", "🛃", "🛄", "🛅", "🚹", "🚺", "🚼", "🚻", "🚮", "🎦", "📶", "🈁", "🔣", "ℹ️", "🔤", "🔡", "🔠", "🆖", "🆗", "🆙", "🆒", "🆕", "🆓", "0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟", "🔢", "#️⃣", "*️⃣", "⏏️", "▶️", "⏸", "⏯", "⏹", "⏺", "⏭", "⏮", "⏩", "⏪", "⏫", "⏬", "◀️", "🔼", "🔽", "➡️", "⬅️", "⬆️", "⬇️", "↗️", "↘️", "↙️", "↖️", "↕️", "↔️", "↪️", "↩️", "⤴️", "⤵️", "🔀", "🔁", "🔂", "🔄", "🔃", "🎵", "🎶", "➕", "➖", "➗", "✖️", "♾", "💲", "💱", "™️", "©️", "®️", "〰️", "➰", "➿", "🔚", "🔙", "🔛", "🔝", "🔜", "✔️", "☑️", "🔘", "🔴", "🟠", "🟡", "🟢", "🔵", "🟣", "⚫", "⚪", "🟤", "🔺", "🔻", "🔸", "🔹", "🔶", "🔷", "🔳", "🔲", "▪️", "▫️", "◾", "◽", "◼️", "◻️", "🟥", "🟧", "🟨", "🟩", "🟦", "🟪", "⬛", "⬜", "🟫", "🔈", "🔇", "🔉", "🔊", "🔔", "🔕", "📣", "📢", "👁‍🗨", "💬", "💭", "🗯", "♠️", "♣️", "♥️", "♦️", "🃏", "🎴", "🀄", "🕐", "🕑", "🕒", "🕓", "🕔", "🕕", "🕖", "🕗", "🕘", "🕙", "🕚", "🕛", "🕜", "🕝", "🕞", "🕟", "🕠", "🕡", "🕢", "🕣", "🕤", "🕥", "🕦", "🕧"]
            ),
            EmojiCategory(
                name: "Flags",
                icon: "🏳️",
                emojis: ["🏳️", "🏴", "🏴‍☠️", "🏁", "🚩", "🏳️‍🌈", "🏳️‍⚧️", "🇺🇳", "🇦🇫", "🇦🇽", "🇦🇱", "🇩🇿", "🇦🇸", "🇦🇩", "🇦🇴", "🇦🇮", "🇦🇶", "🇦🇬", "🇦🇷", "🇦🇲", "🇦🇼", "🇦🇺", "🇦🇹", "🇦🇿", "🇧🇸", "🇧🇭", "🇧🇩", "🇧🇧", "🇧🇾", "🇧🇪", "🇧🇿", "🇧🇯", "🇧🇲", "🇧🇹", "🇧🇴", "🇧🇦", "🇧🇼", "🇧🇷", "🇮🇴", "🇻🇬", "🇧🇳", "🇧🇬", "🇧🇫", "🇧🇮", "🇰🇭", "🇨🇲", "🇨🇦", "🇮🇨", "🇨🇻", "🇧🇶", "🇰🇾", "🇨🇫", "🇹🇩", "🇨🇱", "🇨🇳", "🇨🇽", "🇨🇨", "🇨🇴", "🇰🇲", "🇨🇬", "🇨🇩", "🇨🇰", "🇨🇷", "🇨🇮", "🇭🇷", "🇨🇺", "🇨🇼", "🇨🇾", "🇨🇿", "🇩🇰", "🇩🇯", "🇩🇲", "🇩🇴", "🇪🇨", "🇪🇬", "🇸🇻", "🇬🇶", "🇪🇷", "🇪🇪", "🇪🇹", "🇪🇺", "🇫🇰", "🇫🇴", "🇫🇯", "🇫🇮", "🇫🇷", "🇬🇫", "🇵🇫", "🇹🇫", "🇬🇦", "🇬🇲", "🇬🇪", "🇩🇪", "🇬🇭", "🇬🇮", "🇬🇷", "🇬🇱", "🇬🇩", "🇬🇵", "🇬🇺", "🇬🇹", "🇬🇬", "🇬🇳", "🇬🇼", "🇬🇾", "🇭🇹", "🇭🇳", "🇭🇰", "🇭🇺", "🇮🇸", "🇮🇳", "🇮🇩", "🇮🇷", "🇮🇶", "🇮🇪", "🇮🇲", "🇮🇱", "🇮🇹", "🇯🇲", "🇯🇵", "🎌", "🇯🇪", "🇯🇴", "🇰🇿", "🇰🇪", "🇰🇮", "🇽🇰", "🇰🇼", "🇰🇬", "🇱🇦", "🇱🇻", "🇱🇧", "🇱🇸", "🇱🇷", "🇱🇾", "🇱🇮", "🇱🇹", "🇱🇺", "🇲🇴", "🇲🇰", "🇲🇬", "🇲🇼", "🇲🇾", "🇲🇻", "🇲🇱", "🇲🇹", "🇲🇭", "🇲🇶", "🇲🇷", "🇲🇺", "🇾🇹", "🇲🇽", "🇫🇲", "🇲🇩", "🇲🇨", "🇲🇳", "🇲🇪", "🇲🇸", "🇲🇦", "🇲🇿", "🇲🇲", "🇳🇦", "🇳🇷", "🇳🇵", "🇳🇱", "🇳🇨", "🇳🇿", "🇳🇮", "🇳🇪", "🇳🇬", "🇳🇺", "🇳🇫", "🇰🇵", "🇲🇵", "🇳🇴", "🇴🇲", "🇵🇰", "🇵🇼", "🇵🇸", "🇵🇦", "🇵🇬", "🇵🇾", "🇵🇪", "🇵🇭", "🇵🇳", "🇵🇱", "🇵🇹", "🇵🇷", "🇶🇦", "🇷🇪", "🇷🇴", "🇷🇺", "🇷🇼", "🇼🇸", "🇸🇲", "🇸🇦", "🇸🇳", "🇷🇸", "🇸🇨", "🇸🇱", "🇸🇬", "🇸🇽", "🇸🇰", "🇸🇮", "🇬🇸", "🇸🇧", "🇸🇴", "🇿🇦", "🇰🇷", "🇸🇸", "🇪🇸", "🇱🇰", "🇧🇱", "🇸🇭", "🇰🇳", "🇱🇨", "🇵🇲", "🇻🇨", "🇸🇩", "🇸🇷", "🇸🇿", "🇸🇪", "🇨🇭", "🇸🇾", "🇹🇼", "🇹🇯", "🇹🇿", "🇹🇭", "🇹🇱", "🇹🇬", "🇹🇰", "🇹🇴", "🇹🇹", "🇹🇳", "🇹🇷", "🇹🇲", "🇹🇨", "🇹🇻", "🇻🇮", "🇺🇬", "🇺🇦", "🇦🇪", "🇬🇧", "🏴󐁧󐁢󐁥󐁮󐁧󐁿", "🏴󐁧󐁢󐁳󐁣󐁴󐁿", "🏴󐁧󐁢󐁷󐁬󐁳󐁿", "🇺🇸", "🇺🇾", "🇺🇿", "🇻🇺", "🇻🇦", "🇻🇪", "🇻🇳", "🇼🇫", "🇪🇭", "🇾🇪", "🇿🇲", "🇿🇼"]
            )
        ]
    }
    
    private func loadRecentEmojis() {
        if let recent = UserDefaults.standard.array(forKey: "recent_emojis") as? [String] {
            recentEmojis = recent
        }
        
        // Update the recent category
        if !recentEmojis.isEmpty {
            emojiData[0].emojis = recentEmojis
        }
    }
    
    private func saveRecentEmoji(_ emoji: String) {
        // Remove if already exists
        recentEmojis.removeAll { $0 == emoji }
        
        // Add to beginning
        recentEmojis.insert(emoji, at: 0)
        
        // Keep only last 30
        if recentEmojis.count > 30 {
            recentEmojis = Array(recentEmojis.prefix(30))
        }
        
        // Save to UserDefaults
        UserDefaults.standard.set(recentEmojis, forKey: "recent_emojis")
        
        // Update data
        emojiData[0].emojis = recentEmojis
        
        // Reload recent section
        collectionView.reloadSections(IndexSet(integer: 0))
    }
}

// MARK: - UICollectionViewDataSource
extension EmojiKeyboardViewController: UICollectionViewDataSource {
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return emojiData.count
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return emojiData[section].emojis.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "EmojiCell", for: indexPath) as! EmojiCell
        let emoji = emojiData[indexPath.section].emojis[indexPath.item]
        cell.configure(with: emoji)
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        let header = collectionView.dequeueReusableSupplementaryView(
            ofKind: UICollectionView.elementKindSectionHeader,
            withReuseIdentifier: "Header",
            for: indexPath
        ) as! EmojiSectionHeader
        
        header.configure(with: emojiData[indexPath.section].name)
        return header
    }
}

// MARK: - UICollectionViewDelegateFlowLayout
extension EmojiKeyboardViewController: UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        let width = (collectionView.frame.width - 20 - 7 * 8) / 8
        return CGSize(width: width, height: width)
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        return CGSize(width: collectionView.frame.width, height: 30)
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let emoji = emojiData[indexPath.section].emojis[indexPath.item]
        delegate?.emojiKeyboard(self, didSelectEmoji: emoji)
        saveRecentEmoji(emoji)
        HapticManager.shared.triggerSelection()
    }
}

// MARK: - EmojiCategoryBarDelegate
extension EmojiKeyboardViewController: EmojiCategoryBarDelegate {
    func categoryBar(_ bar: EmojiCategoryBar, didSelectCategory index: Int) {
        let indexPath = IndexPath(item: 0, section: index)
        collectionView.scrollToItem(at: indexPath, at: .top, animated: true)
    }
}

// MARK: - Supporting Classes
class EmojiCell: UICollectionViewCell {
    private let label = UILabel()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupCell()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupCell()
    }
    
    private func setupCell() {
        label.textAlignment = .center
        label.font = .systemFont(ofSize: 28)
        label.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(label)
        
        NSLayoutConstraint.activate([
            label.centerXAnchor.constraint(equalTo: contentView.centerXAnchor),
            label.centerYAnchor.constraint(equalTo: contentView.centerYAnchor)
        ])
    }
    
    func configure(with emoji: String) {
        label.text = emoji
    }
    
    override var isHighlighted: Bool {
        didSet {
            UIView.animate(withDuration: 0.1) {
                self.transform = self.isHighlighted ? CGAffineTransform(scaleX: 1.2, y: 1.2) : .identity
            }
        }
    }
}

class EmojiSectionHeader: UICollectionReusableView {
    private let titleLabel = UILabel()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupHeader()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupHeader()
    }
    
    private func setupHeader() {
        titleLabel.font = .systemFont(ofSize: 14, weight: .semibold)
        titleLabel.textColor = .secondaryLabel
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(titleLabel)
        
        NSLayoutConstraint.activate([
            titleLabel.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 10),
            titleLabel.centerYAnchor.constraint(equalTo: centerYAnchor)
        ])
    }
    
    func configure(with title: String) {
        titleLabel.text = title
    }
}

class EmojiCategoryBar: UIView {
    weak var delegate: EmojiCategoryBarDelegate?
    private var buttons: [UIButton] = []
    private let scrollView = UIScrollView()
    private let stackView = UIStackView()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupBar()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupBar()
    }
    
    private func setupBar() {
        backgroundColor = .systemBackground
        layer.borderWidth = 0.5
        layer.borderColor = UIColor.separator.cgColor
        
        scrollView.showsHorizontalScrollIndicator = false
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(scrollView)
        
        stackView.axis = .horizontal
        stackView.spacing = 16
        stackView.translatesAutoresizingMaskIntoConstraints = false
        scrollView.addSubview(stackView)
        
        NSLayoutConstraint.activate([
            scrollView.leadingAnchor.constraint(equalTo: leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: trailingAnchor),
            scrollView.topAnchor.constraint(equalTo: topAnchor),
            scrollView.bottomAnchor.constraint(equalTo: bottomAnchor),
            
            stackView.leadingAnchor.constraint(equalTo: scrollView.leadingAnchor, constant: 16),
            stackView.trailingAnchor.constraint(equalTo: scrollView.trailingAnchor, constant: -16),
            stackView.topAnchor.constraint(equalTo: scrollView.topAnchor),
            stackView.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
            stackView.heightAnchor.constraint(equalTo: scrollView.heightAnchor)
        ])
        
        setupCategories()
    }
    
    private func setupCategories() {
        let categories = ["🕐", "😀", "🐶", "🍔", "⚽", "🚗", "💡", "❤️", "🏳️"]
        
        for (index, icon) in categories.enumerated() {
            let button = UIButton(type: .system)
            button.setTitle(icon, for: .normal)
            button.titleLabel?.font = .systemFont(ofSize: 24)
            button.tag = index
            button.addTarget(self, action: #selector(categoryTapped(_:)), for: .touchUpInside)
            buttons.append(button)
            stackView.addArrangedSubview(button)
        }
    }
    
    @objc private func categoryTapped(_ sender: UIButton) {
        delegate?.categoryBar(self, didSelectCategory: sender.tag)
        
        // Update selection state
        buttons.forEach { $0.alpha = $0 == sender ? 1.0 : 0.5 }
    }
}

protocol EmojiCategoryBarDelegate: AnyObject {
    func categoryBar(_ bar: EmojiCategoryBar, didSelectCategory index: Int)
}

protocol EmojiKeyboardDelegate: AnyObject {
    func emojiKeyboard(_ keyboard: EmojiKeyboardViewController, didSelectEmoji emoji: String)
}
```