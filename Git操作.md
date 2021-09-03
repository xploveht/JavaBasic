# Git操作

```shell

   git init
   git add .
   git commit -m "first commit"
   git config --global user.email "154864162@qq.com"
   git config --global user.name "xp"
   git commit -m "first commit"
   
   git remote add origin https://github.com/xploveht/JavaBasic.git
   git push -u origin master
   git remote rm origin
   git remote add origin https://github.com/xploveht/JavaBasic.git
   git push -u origin master
   git push -u origin main
   
   完美解决 fatal: unable to access 'https://github.com/.../.git': Could not resolve host: 		github.com
   git config --global --unset http.proxy
   git config --global --unset https.proxy
   
   git config --global http.sslVerify "false"

   
   25  git push -u origin main
   
   
   设置token：remote: Support for password authentication was removed on August 13, 2021.
   
   30  git remote set-url origin https://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@github.com/xploveht/JavaBasic.git
   31  git pull
   32  git pull
   33  git config --global --unset http.proxy
   34  git config --global --unset https.proxy
   35  git pull
   36  history

```

