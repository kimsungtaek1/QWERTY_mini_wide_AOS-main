# 키보드 키 커스텀 배치 가이드

## 키 위치 조정 방법

### 1. Gravity 설정
키 내부 텍스트의 기본 위치를 설정합니다.

- `ltTextGravity`: 왼쪽 위 텍스트 (기본값: 3 = LEFT)
- `rtTextGravity`: 오른쪽 위 텍스트 (기본값: 5 = RIGHT)
- `rbTextGravity`: 오른쪽 아래 텍스트 (기본값: 85 = BOTTOM | RIGHT)

#### Gravity 값 참고
- `3`: LEFT
- `5`: RIGHT
- `17`: CENTER
- `48`: BOTTOM
- `80`: BOTTOM
- `85`: BOTTOM | RIGHT (80 + 5)
- `83`: BOTTOM | LEFT (80 + 3)
- `50`: BOTTOM | CENTER_HORIZONTAL (48 + 2)

### 2. Margin을 통한 미세 조정
Gravity로 기본 위치를 정한 후, margin으로 세밀하게 조정합니다.

#### 오른쪽 아래 텍스트 (rb) 위치 조정 예시
```kotlin
// Q를 오른쪽 아래에서 왼쪽으로 살짝 이동
rbTextGravity = 85,  // BOTTOM | RIGHT
rbTextMarginRight = 20,  // 오른쪽에서 20픽셀 안쪽으로
rbTextMarginBottom = 10  // 아래에서 10픽셀 위로
```

### 3. 주의사항

#### XML 레이아웃 파일 수정 필요
`key_button.xml`에서 기본 margin 값이 설정되어 있으면 코드에서 설정한 값이 무시될 수 있습니다.
```xml
<!-- 수정 전 (margin 값이 하드코딩됨) -->
<TextView
    android:id="@+id/tv_rb"
    android:layout_marginEnd="4dp"
    android:layout_marginBottom="2dp"
    ... />

<!-- 수정 후 (margin 제거) -->
<TextView
    android:id="@+id/tv_rb"
    ... />
```

#### Margin 값의 이해
- `ltTextMarginLeft`: 값이 클수록 오른쪽으로 이동 (왼쪽에서부터의 거리)
- `ltTextMarginTop`: 값이 클수록 아래로 이동 (음수값은 위로 이동)
- `rbTextMarginRight`: 값이 클수록 왼쪽으로 이동 (오른쪽 끝에서부터의 거리)
- `rbTextMarginBottom`: 값이 클수록 위로 이동 (아래쪽 끝에서부터의 거리)

#### Margin 값의 한계
- `rbTextGravity = 17` (CENTER)일 때 `rbTextMarginLeft`가 제대로 작동하지 않을 수 있음
- 대신 `rbTextGravity = 85` (BOTTOM | RIGHT)를 사용하고 `rbTextMarginRight`로 조정하는 것을 권장

### 4. 실제 적용 예시

```kotlin
// 대문자 Q 위치 조정
KeyModel(
    KeyType.LETTER, 
    ltText = "W", 
    rtText = "˙", 
    rbText = "Q",
    // ... 다른 설정들 ...
    rbTextGravity = 85,  // 오른쪽 아래 정렬
    rbTextMarginRight = 20,  // 오른쪽에서 20픽셀 안쪽
    rbTextMarginBottom = 10  // 아래에서 10픽셀 위로
)
```

이 방법을 통해 키보드의 각 서브레터 위치를 정밀하게 조정할 수 있습니다.