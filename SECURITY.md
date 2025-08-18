# 보안 정책 (Security Policy)

## 지원 버전
다음 버전에 대해 보안 패치를 제공합니다:
- 최신(main)과 최근 LTS 릴리스
- 이전 릴리스는 중대(Critical) 취약점에 한해 케이스별 지원

## 취약점 신고 방법
- 선호: GitHub **Private Vulnerability Reporting(PVR)** 을 이용해 비공개로 신고해 주세요.
  - 리포지토리의 **Security → Report a vulnerability** 버튼을 사용합니다.
- 대안: security@example.com 으로 메일(가능하면 PGP 사용) 주세요.

### 암호화
- PGP: `0xAAAAAAAA` (https://keys.openpgp.org/…)

## 응답 타임라인(목표)
- 접수 확인: 48시간 이내
- 재현/영향 분석: 5영업일 이내
- 수정/패치 릴리스: 심각도에 따라 30일(High/Critical) 이내 목표
- 공개 공지: 패치/우회책 제공 직후 **Security Advisory** 로 게시

## 공개(Disclosure) 정책
- 패치가 준비되면 GitHub **Repository Security Advisory** 로 상세 내역(영향 범위, 완화책, 크레딧, CVE)을 공개합니다.
- CVE가 필요할 경우 해당 어드바이저리에서 요청/연계합니다.

## 안전항(Safe Harbor)
선의의 보안 연구자는 합리적인 선에서 테스트를 수행하고, 데이터를 악용하지 않으며, 위 절차에 따라 신속히 보고할 경우 법적 조치를 취하지 않습니다. 서비스 중단, 데이터 파괴/유출, 개인정보 접근 시도는 금지됩니다.

## 범위(Out of Scope)
- 소셜 엔지니어링, 물리적 공격, 서드파티 서비스의 이슈 등은 범위에서 제외될 수 있습니다.

## 크레딧
책임 있는 공개에 협력해 주신 연구자분들께 감사드리며, 공개 어드바이저리에 기여를 표기합니다.
---
# Security Policy

## Supported Versions
We provide security patches for the following versions:
- Latest (main) and recent LTS releases
- Older releases may only receive fixes for Critical vulnerabilities on a case-by-case basis

## Reporting a Vulnerability
- Preferred: Use GitHub **Private Vulnerability Reporting (PVR)** to report confidentially.  
  - Go to **Security → Report a vulnerability** in this repository.  
- Alternative: Email us at security@example.com (PGP encryption recommended).

### Encryption
- PGP: `0xAAAAAAAA` (https://keys.openpgp.org/…)

## Response Timeline (Target)
- Acknowledgement: within 48 hours  
- Reproduction & impact analysis: within 5 business days  
- Fix/patch release: within 30 days for High/Critical issues  
- Public disclosure: via **Security Advisory** immediately after fix/workaround is available  

## Disclosure Policy
- Once a fix is ready, we will publish a **Repository Security Advisory** including details (impact, mitigation, credits, CVE).  
- CVEs will be requested/linked through the advisory when applicable.  

## Safe Harbor
We will not pursue legal action against security researchers who:
- Act in good faith,  
- Do not exploit data, and  
- Report issues promptly via the methods above.  
Denial-of-service, data destruction/leakage, and attempts to access personal information are strictly prohibited.  

## Out of Scope
- Social engineering, physical attacks, or third-party service issues are considered out of scope.  

## Credits
We thank researchers who practice responsible disclosure. Contributors will be credited in the published advisories.
