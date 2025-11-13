# 멀티 스테이지 빌드: 빌드 스테이지
FROM node:22-alpine AS builder

WORKDIR /app

# 패키지 파일 복사 및 의존성 설치
COPY package*.json ./
RUN npm ci

# 소스 코드 복사
COPY . .

# 환경 변수 설정 (빌드 타임)
ARG VITE_API_URL
ENV VITE_API_URL=${VITE_API_URL}

# 프로덕션 빌드
RUN npm run build

# 프로덕션 스테이지: Nginx
FROM nginx:alpine

# Nginx 설정 파일 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 빌드된 파일을 Nginx 정적 파일 디렉토리로 복사
COPY --from=builder /app/dist /usr/share/nginx/html

# 헬스체크
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:80/ || exit 1

# Nginx 기본 포트
EXPOSE 80

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]
